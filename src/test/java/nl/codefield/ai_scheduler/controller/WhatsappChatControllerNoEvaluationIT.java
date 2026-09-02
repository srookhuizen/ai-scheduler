package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.config.TestConfig;
import nl.codefield.ai_scheduler.dto.*;
import nl.codefield.ai_scheduler.entity.Customer;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import nl.codefield.ai_scheduler.service.BookingService;
import nl.codefield.ai_scheduler.service.CalendarService;
import nl.codefield.ai_scheduler.service.CustomerService;
import nl.codefield.ai_scheduler.service.WhatsappService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "openai"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({
        //DockerTestConfig.class
        TestConfig.class
})
@Disabled
class WhatsappChatControllerNoEvaluationIT {

    public static final String PHONE_NUMBER = "31612345678";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository customerRepository;

    @MockitoSpyBean
    private WhatsappService whatsappService;

    @MockitoSpyBean
    private CalendarService calendarService;

    @MockitoSpyBean
    private BookingService bookingService;

    @MockitoSpyBean
    private CustomerService customerService;

    @BeforeEach
    void cleanState() throws IOException {
        Mockito.reset(whatsappService, calendarService, bookingService);
        customerRepository.deleteAll();
        calendarService.deleteAllEvents();
    }

    @Test
    void handleIncomingMessage() throws Exception {
        sendAndReceive("Hello! I need a haircut.");
        verify(customerService, atLeastOnce()).findByPhoneNumber(PHONE_NUMBER);

        // Step 2, register the new customer
        // Reset service mocks to capture the state change cleanly for the second turn
        reset(whatsappService);
        String userOnboardingDetailsPrompt = "My name is John Doe, my email address is john.doe@example.com, and I am a male.";
        sendAndReceive(userOnboardingDetailsPrompt);

        verify(bookingService).registerNewCustomer(argThat(gender -> gender.equalsIgnoreCase("John Doe")),
                argThat(gender -> gender.equalsIgnoreCase("male")),
                eq(PHONE_NUMBER),
                eq("john.doe@example.com"));

        // Step 3, date and time for the appointment
        reset(whatsappService);
        sendAndReceive("Can we put it for tomorrow 3 PM?");

        verify(calendarService).getEvents(any(String.class), any(LocalDateTime.class));

        LocalDateTime expectedStartDateTime = LocalDateTime.parse("2026-08-05T15:00:00");
        LocalDateTime expectedEndDateTime = expectedStartDateTime.plusMinutes(30);
        verify(calendarService, timeout(2000)).addEvent(
                eq("Haircut: John Doe"),
                any(String.class),
                eq(expectedStartDateTime),
                eq(expectedEndDateTime)
        );
    }

    @Test
    void handleIncomingMessage_knownCustomer() throws Exception {
        Customer customer = Customer.builder().firstName("John Doe").gender("Male")
                .phoneNumber(PHONE_NUMBER).email("john.doe@example.com").build();
        customerRepository.save(customer);

        sendAndReceive("I want my hair cut.");
        verify(customerService, atLeastOnce()).findByPhoneNumber(PHONE_NUMBER);

        // Step 3, date and time for the appointment
        reset(whatsappService);
        sendAndReceive("Can we put it for tomorrow 3 PM?");

        verify(calendarService).getEvents(any(String.class), any(LocalDateTime.class));

        LocalDateTime expectedStartDateTime = LocalDateTime.parse("2026-08-05T15:00:00");
        LocalDateTime expectedEndDateTime = expectedStartDateTime.plusMinutes(30);
        verify(calendarService, timeout(2000)).addEvent(
                eq("Haircut: John Doe"),
                any(String.class),
                eq(expectedStartDateTime),
                eq(expectedEndDateTime)
        );
    }

    @Test
    void handleIncomingMessage_knownCustomer_andExistingEvent() throws Exception {
        Customer customer = Customer.builder().firstName("John Doe").gender("Male")
                .phoneNumber(PHONE_NUMBER).email("john.doe@example.com").build();
        customerRepository.save(customer);

        LocalDateTime start = LocalDateTime.parse("2026-08-05T15:00:00");
        LocalDateTime end = start.plusMinutes(30);
        // calendarService.addEvent("Haircut: John Doe", "Haircut: John Doe", start, end);

        sendAndReceive("Hi, i want to cut my hair.`");
        verify(customerService, atLeastOnce()).findByPhoneNumber(PHONE_NUMBER);

        // Step 3, date and time for the appointment
        reset(whatsappService);
        sendAndReceive("Can we put it for tomorrow 3 PM?");

        reset(whatsappService);
        sendAndReceive("Is it possible to put it half an hour later?");

        verify(calendarService, atLeastOnce()).getEvents(any(String.class), any(LocalDateTime.class));

        LocalDateTime expectedStart = LocalDateTime.parse("2026-08-05T15:30:00");
        LocalDateTime expectedEnd = expectedStart.plusMinutes(30);
        verify(calendarService, timeout(2000)).addEvent(
                eq("Haircut: John Doe"),
                any(String.class),
                eq(expectedStart),
                eq(expectedEnd)
        );
    }

    @Test
    void handleIncomingMessage_dutch() throws Exception {
        sendAndReceive("Hoi, ik wil mijn haar laten knippen");
        verify(customerService, atLeastOnce()).findByPhoneNumber(PHONE_NUMBER);

        // Step 2, register the new customer
        // Reset service mocks to capture the state change cleanly for the second turn
        reset(whatsappService);
        String userOnboardingDetailsPrompt = "Mijn naam is Jan Janssen, mijn email is jan.janssen@voorbeeld.nl, en ik ben een man.";
        sendAndReceive(userOnboardingDetailsPrompt);

        verify(bookingService).registerNewCustomer(argThat(gender -> gender.equalsIgnoreCase("Jan Janssen")),
                argThat(gender -> gender.equalsIgnoreCase("male")),
                eq(PHONE_NUMBER),
                eq("jan.janssen@voorbeeld.nl"));

        // Step 3, date and time for the appointment
        reset(whatsappService);
        sendAndReceive("Kan het morgen om 3 uur 'smiddags");

        verify(calendarService).getEvents(any(String.class), any(LocalDateTime.class));

        LocalDateTime expectedStartDateTime = LocalDateTime.parse("2026-08-05T15:00:00");
        LocalDateTime expectedEndDateTime = expectedStartDateTime.plusMinutes(30);
        verify(calendarService, timeout(2000)).addEvent(
                eq("Haircut: Jan Janssen"),
                any(String.class),
                eq(expectedStartDateTime),
                eq(expectedEndDateTime)
        );
    }

    private void sendAndReceive(String userPrompt) throws Exception {
        String phoneNumber = PHONE_NUMBER;

        IncomingMessageText incomingMessage = new IncomingMessageText();
        incomingMessage.setBody(userPrompt);

        IncomingMessage message = new IncomingMessage();
        message.setFrom(phoneNumber);
        message.setText(incomingMessage);

        WebhookChangeValue value = new WebhookChangeValue();
        value.setMessages(Collections.singletonList(message));

        WebhookChange change = new WebhookChange();
        change.setValue(value);

        WebhookEntry entry = new WebhookEntry();
        entry.setChanges(Collections.singletonList(change));

        WebhookPayload payload = new WebhookPayload();
        payload.setEntry(Collections.singletonList(entry));

        String requestBodyJson = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(whatsappService, timeout(600000).times(1))
                .sendTextMessage(eq(phoneNumber), messageCaptor.capture());

        messageCaptor.getValue();
    }
}
