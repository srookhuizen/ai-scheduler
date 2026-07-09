package nl.codefield.ai_scheduler.service;

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

    @Tool(name = "checkIncomingCustomerState", description = "Check if the incoming WhatsApp sender already exists in the database. Returns routing directives for the agent.")
    public String checkIncomingCustomerState(
            @ToolParam(description = "The raw senderNumber extracted from the WhatsApp incoming webhook metadata envelope.") String senderNumber) {

        log.info("Checking incoming WhatsApp sender authentication token state: {}", senderNumber);

        Optional<Customer> customerByWhatsapp = customerRepository.findByPhoneNumber(senderNumber);
        if (customerByWhatsapp.isPresent()) {
            Customer customer = customerByWhatsapp.get();
            log.info("Returning customer detected: {} (Email: {})", customer.getName(), customer.getEmail());
            return "MATCH_FOUND: This is a returning customer named " + customer.getName() +
                    " with email " + customer.getEmail() + ". You may skip data onboarding questions and proceed straight to scheduling loops.";
        }

        log.info("WhatsApp number unmapped. Instructing agent to request an email lookup sequence.");
        return "NO_WHATSAPP_MATCH: This phone number is not linked to a profile. " +
                "ACTION REQUIRED: Ask the customer politely to provide their email address so we can check for an existing account profile.";
    }

    @Tool(name = "registerNewCustomer", description = "Register or update a customer profile in the database with their personal details.")
    @Transactional
    public String registerNewCustomer(
            @ToolParam(description = "The customer's complete full name.") String name,
            @ToolParam(description = "The customer's gender (e.g., Male, Female, Other).") String gender,
            @ToolParam(description = "The customer's contact telephone phone number.") String phone,
            @ToolParam(description = "The customer's personal email address.") String email) {

        log.info("LLM tool execution: registerNewCustomer for profile: {}", email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseGet(() -> Customer.builder().email(email).build());

        customer.setName(name);
        customer.setGender(gender);
        customer.setPhoneNumber(phone);

        customerRepository.save(customer);
        return "SUCCESS: Customer " + name + " has been successfully registered in the database.";
    }

    @Tool(name = "saveAppointmentToDb", description = "Save the finalized appointment record into the local internal database.")
    @Transactional
    public String saveAppointmentToDb(
            @ToolParam(description = "The main title summary of the appointment (e.g., 'Haircut').") String summary,
            @ToolParam(description = "The customer's registered email address to link the relational foreign key.") String email,
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

    @Tool(name = "getCustomerByPhoneNumber", description = "Find a customer by phoneNumber string.")
    public Optional<Customer> getCustomerByPhoneNumber(@ToolParam(description = "The plain phone text query.") String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }
}
