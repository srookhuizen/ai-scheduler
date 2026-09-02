package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.event.AppointmentSavedEvent;
import nl.codefield.ai_scheduler.entity.Appointment;
import nl.codefield.ai_scheduler.entity.Customer;
import nl.codefield.ai_scheduler.entity.Service;
import nl.codefield.ai_scheduler.repository.AppointmentRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
    private final CustomerService customerService;
    private final AppointmentRepository appointmentRepository;
    private final ServiceService serviceService;
    private final ApplicationEventPublisher eventPublisher;

    @Tool(name = "registerNewCustomer", description = "Register or update a customer profile in the database with their personal details.")
    @Transactional
    public String registerNewCustomer(
            @ToolParam(description = "The customer's complete full name.") String name,
            @ToolParam(description = "The customer's gender (e.g., Male, Female, Other).") String gender,
            @ToolParam(description = "The customer's contact telephone phone number.") String phoneNumber,
            @ToolParam(description = "The customer's personal email address.") String email) {


        Customer customer = customerService.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> Customer.builder().firstName(name).gender(gender).phoneNumber(phoneNumber).email(email).build());
        log.info("LLM tool execution: registerNewCustomer for profile: {}", customer);

        CustomerDTO savedCustomer = customerService.save(customer);
        log.info("Successfully registered customer: {}", savedCustomer);
        return "SUCCESS: Customer " + savedCustomer.getFirstName() + " has been successfully registered in the database.";
    }


    @Tool(name = "saveAppointmentToDb", description = "Save the finalized appointment record into the local internal database. Java will automatically compute the ending time.")
    @Transactional
    public String saveAppointmentToDb(
            @ToolParam(description = "The specific name of the service requested by the user.") String service,
            @ToolParam(description = "The customer's registered phoneNumber.") String phoneNumber,
            @ToolParam(description = "The exact start date and time string in ISO-8601 format (YYYY-MM-DDTHH:mm:ss).") String start) {

        log.info("LLM tool execution: saveAppointmentToDb for customer phoneNumber: {} for service: {} at start: {}", phoneNumber, service, start);

        Customer customer = customerService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("DATABASE ERROR: Cannot map appointment. Profile not found for phoneNumber: " + phoneNumber));

        LocalDateTime localStart = LocalDateTime.parse(start, ISO_FORMATTER);
        Service serviceObj = serviceService.findByName(service);
        LocalDateTime localEnd = localStart.plusMinutes(serviceObj.getDuration());

        Appointment appointment = Appointment.builder()
                .service(serviceObj)
                .appointmentStart(localStart)
                .appointmentEnd(localEnd)
                .customer(customer)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        eventPublisher.publishEvent(new AppointmentSavedEvent(this, savedAppointment));

        return "SUCCESS: Appointment record for " + service + " has been committed. Calculated slot: " + localStart + " to " + localEnd;
    }
}
