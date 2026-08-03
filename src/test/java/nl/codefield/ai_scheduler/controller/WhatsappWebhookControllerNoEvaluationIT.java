package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.config.TestConfig;
import nl.codefield.ai_scheduler.dto.IncomingMessage;
import nl.codefield.ai_scheduler.dto.IncomingMessageText;
import nl.codefield.ai_scheduler.dto.WebhookChange;
import nl.codefield.ai_scheduler.dto.WebhookChangeValue;
import nl.codefield.ai_scheduler.dto.WebhookEntry;
import nl.codefield.ai_scheduler.dto.WebhookPayload;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import nl.codefield.ai_scheduler.service.BookingService;
import nl.codefield.ai_scheduler.service.CalendarService;
import nl.codefield.ai_scheduler.service.WhatsappService;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "ollama"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({
        //DockerTestConfig.class
        TestConfig.class
})
@Ignore
class WhatsappWebhookControllerNoEvaluationIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CustomerRepository customerRepository;

    @MockitoSpyBean
    private WhatsappService whatsappService;

    @MockitoSpyBean
    private CalendarService calendarService;

    @MockitoSpyBean
    private BookingService bookingService;

    @BeforeEach
    void cleanState() {
        Mockito.reset(whatsappService, calendarService, bookingService);
        customerRepository.deleteAll();
    }

    @Test
    void handleIncomingMessage() throws Exception {
        String initializationPrompt = "Hello! I need a haircut.";
        sendAndReceive(initializationPrompt);
        verify(bookingService).findCustomerByPhoneNumber("31612345678");

        // Step 2, register the new customer
        // Reset service mocks to capture the state change cleanly for the second turn
        reset(whatsappService);
        String userOnboardingDetailsPrompt = "My name is John Doe, my email address is john.doe@example.com, and I am a male.";
        sendAndReceive(userOnboardingDetailsPrompt);

        verify(bookingService).registerNewCustomer(argThat(gender -> gender.equalsIgnoreCase("John Doe")),
                argThat(gender -> gender.equalsIgnoreCase("male")),
                eq("31612345678"),
                eq("john.doe@example.com"));

        // Step 3, date and time for the appointment
        reset(whatsappService);
        String dateAndTimePrompt = "Can we put it for tomorrow 3 PM?";
        sendAndReceive(dateAndTimePrompt);

        verify(calendarService).getEvents(any(LocalDateTime.class), any(LocalDateTime.class));

    }

    private String sendAndReceive(String userPrompt) throws Exception {
        String phoneNumber = "31612345678";

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

        return messageCaptor.getValue();
    }
}
