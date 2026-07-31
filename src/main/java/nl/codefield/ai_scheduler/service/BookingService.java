package nl.codefield.ai_scheduler.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.model.Appointment;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.repository.AppointmentRepository;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;

    @Tool(name = "findCustomerByPhoneNumber", description = "Check if a Customer exist in the database for the given phoneNumber.")
    public Customer findCustomerByPhoneNumber(@NotNull @ToolParam(description = "The phone number of the customer") String phoneNumber) {

        log.info("Finding customer for phoneNumber: {}", phoneNumber);

        Optional<Customer> optionalCustomer = customerRepository.findByPhoneNumber(phoneNumber);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            log.info("Customer found {}", customer);
            return customer;
        }

        log.info("Customer not found for: {}", phoneNumber);
        return null;
    }

    @Tool(name = "registerNewCustomer", description = "Register or update a customer profile in the database with their personal details.")
    @Transactional
    public String registerNewCustomer(
            @ToolParam(description = "The customer's complete full name.") String name,
            @ToolParam(description = "The customer's gender (e.g., Male, Female, Other).") String gender,
            @ToolParam(description = "The customer's contact telephone phone number.") String phoneNumber,
            @ToolParam(description = "The customer's personal email address.") String email) {


        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> Customer.builder().name(name).gender(gender).phoneNumber(phoneNumber).email(email).build());
        log.info("LLM tool execution: registerNewCustomer for profile: {}", customer);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully registered customer: {}", savedCustomer);
        return "SUCCESS: Customer " + savedCustomer.getName() + " has been successfully registered in the database.";
    }


    @Tool(name = "saveAppointmentToDb", description = "Save the finalized appointment record into the local internal database.")
    @Transactional
    public String saveAppointmentToDb(
            @ToolParam(description = "The main title summary of the appointment.") String summary,
            @ToolParam(description = "The customer's registered email address.") String email,
            @ToolParam(description = "The exact start date and time string in ISO-8601 format.") String start,
            @ToolParam(description = "The exact end date and time string in ISO-8601 format.") String end) {

        log.info("LLM tool execution: saveAppointmentToDb for customer email: {}", email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("DATABASE ERROR: Cannot map appointment. Profile not found for email: " + email));

        LocalDateTime localStart = LocalDateTime.parse(start, ISO_FORMATTER);
        LocalDateTime localEnd = LocalDateTime.parse(end, ISO_FORMATTER);

        Appointment appointment = Appointment.builder()
                .serviceType(summary)
                .appointmentStart(localStart)
                .appointmentEnd(localEnd)
                .customer(customer)
                .build();

        appointmentRepository.save(appointment);
        return "SUCCESS: Appointment record has been securely committed to the local database repository.";
    }

    @Tool(name = "getCustomerByEmail", description = "Find an existing customer account by their email address profile key.")
    public String getCustomerByEmail(@ToolParam(description = "The email string to query.") String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer c = customerOpt.get();
            return "EMAIL_MATCH_FOUND: Profile exists. Name: " + c.getName() + ", Gender: " + c.getGender() + ", Phone: " + c.getPhoneNumber() + ". You may link this session and proceed to scheduling loops.";
        }
        return "EMAIL_NOT_FOUND: No profile exists for this email. ACTION REQUIRED: This is a completely brand new client. You must ask for their full name, gender, and contact phone number, and then call registerNewCustomer.";
    }
}
