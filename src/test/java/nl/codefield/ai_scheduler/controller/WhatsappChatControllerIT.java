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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
class WhatsappChatControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChatClient chatClient;

    @Autowired
    @Qualifier("evaluationChatClient")
    private ChatClient evaluationChatClient; // Secondary evaluation client (bespoke-minicheck)

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

    // ==========================================
    // GET /webhook Verification Test
    // ==========================================
    @Test
    void verifyWebhook_Success() throws Exception {
        mockMvc.perform(get("/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "my-secret-wce-verify-token")
                        .param("hub.challenge", "challenge_granted_123"))
                .andExpect(status().isOk())
                .andExpect(content().string("challenge_granted_123"));
    }

    // ==========================================
    // POST /webhook E2E Test + Evaluators
    // ==========================================
    @Test
    void handleIncomingMessage_WithRealContainersAndEvaluators() throws Exception {
        String initializationPrompt = "Hello! I need a haircut.";
        String aiGeneratedResponse = sendAndReceive(initializationPrompt);

        // the template variables query, response and context are filled by spring
        PromptTemplate relaxedRelevancyPrompt = new PromptTemplate("""
            Determine if the response is a valid next step to help the user request a haircut.
            Answer only YES or NO.
            
            User Query: {query}
            Assistant Response: {response}
            Context: {context}
            
            Answer: """);

        RelevancyEvaluator relevancyEvaluator = RelevancyEvaluator.builder()
                .chatClientBuilder(evaluationChatClient.mutate())
                .promptTemplate(relaxedRelevancyPrompt)
                .build();

        String cleanContextText = """
            The assistant must onboard unknown callers by requesting their full name, email address, and gender
            before allowing them to schedule a haircut slot.""";
        Document cleanRelevancyContext = new Document(cleanContextText);

        EvaluationRequest relevancyRequest = new EvaluationRequest(initializationPrompt, List.of(cleanRelevancyContext), aiGeneratedResponse);
        EvaluationResponse relevancyResponse = relevancyEvaluator.evaluate(relevancyRequest);

        Assertions.assertTrue(relevancyResponse.isPass(),
                "The model response failed Spring AI relevancy criteria! Feedback: " + relevancyResponse.getFeedback());

        FactCheckingEvaluator factCheckingEvaluator = FactCheckingEvaluator.builder(evaluationChatClient.mutate()).build();

        String businessFactContext = """
            The system allows clients to look up and book automated calendar slots.
            If a client's phone number is not found in the database, the system must collect their full name,
            email address, and gender to register them as a new customer.
            The user's phone number is already verified through WhatsApp metadata,
            so additional phone input collection rules are flexible or optional.
            """;
        Document factTruthDocument = new Document(businessFactContext);

        EvaluationRequest factRequest = new EvaluationRequest(initializationPrompt, List.of(factTruthDocument), aiGeneratedResponse);
        EvaluationResponse factResponse = factCheckingEvaluator.evaluate(factRequest);

        Assertions.assertTrue(factResponse.isPass(),
                "The model response failed Spring AI fact-checking parameters! Feedback: " + factResponse.getFeedback());

        // Step 2, register the new customer
        // Reset service mocks to capture the state change cleanly for the second turn
        reset(whatsappService);
        String userOnboardingDetailsPrompt = "My name is John Doe, my email address is john.doe@example.com, and I am a male.";
        String nextWorkflowResponse = sendAndReceive(userOnboardingDetailsPrompt);

        verify(bookingService).registerNewCustomer(argThat(gender -> gender.equalsIgnoreCase("John Doe")),
                argThat(gender -> gender.equalsIgnoreCase("male")),
                eq("31612345678"),
                eq("john.doe@example.com"));

        String registrationContextTruth = """
            The customer registration workflow is complete.
            The assistant acknowledges that the user wants a haircut.
            The assistant is now asking the user for their preferred appointment date and time.
            """;

        factTruthDocument = new Document(registrationContextTruth);

        factRequest = new EvaluationRequest(userOnboardingDetailsPrompt, List.of(factTruthDocument), nextWorkflowResponse);
        factResponse = factCheckingEvaluator.evaluate(factRequest);

        Assertions.assertTrue(factResponse.isPass(),
                "The registration-to-booking transition failed fact-checking parameters! Feedback: " + factResponse.getFeedback());

        // Step 3, date and time for the appointment
        reset(whatsappService);
        String dateAndTimePrompt = "Can we put it for tomorrow 3 PM?";
        String dateAndTimeResponse = sendAndReceive(dateAndTimePrompt);

        verify(calendarService).getEvents(any(String.class), any(LocalDateTime.class));

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
