package nl.tss.ai_scheduler.controller;

import nl.tss.ai_scheduler.service.CalendarService;
import nl.tss.ai_scheduler.service.WhatsappService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers // Handles automated Docker lifecycle processing
class WhatsappWebhookControllerTestContainersIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WhatsappService whatsappService;

    @MockitoBean
    private CalendarService calendarService;

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:whatsapp-incoming-message.json")
    private Resource whatsappIncomingMessageResource;

    // Spins up a real, local Ollama engine inside Docker to handle AI mocking safely
    @Container
    static GenericContainer<?> ollamaContainer = new GenericContainer<>("ollama/ollama:latest")
            .withExposedPorts(11434);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException, InterruptedException {
        // Automatically download and extract the lightweight model inside the Docker instance
        ollamaContainer.execInContainer("ollama", "run", "tinydolphin");

        String ollamaEndpoint = String.format("http://%s:%d",
                ollamaContainer.getHost(), ollamaContainer.getMappedPort(11434));

        // Dynamically bind the temporary Docker container ports to Spring AI
        registry.add("spring.ai.ollama.base-url", () -> ollamaEndpoint);
        registry.add("spring.ai.ollama.chat.options.model", () -> "tinydolphin");
    }

    // --- TESTCASE 1: Webhook Handshake Verification (Success Path) ---
    @Test
    void testWebhookVerification_Success() throws Exception {
        mockMvc.perform(get("/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "my-secret-wce-verify-token")
                        .param("hub.challenge", "123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string("123456789"));
    }

    // --- TESTCASE 2: Webhook Handshake Verification (Failure Path) ---
    @Test
    void testWebhookVerification_InvalidToken() throws Exception {
        mockMvc.perform(get("/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "WRONG_TOKEN")
                        .param("hub.challenge", "123456789"))
                .andExpect(status().isForbidden());
    }

    // --- TESTCASE 3: End-to-End Payload Pipeline with Live Ollama Processing ---
    @Test
    void testHandleIncomingMessage_TriggersRealOllamaAndSendsWhatsapp() throws Exception {
        String incomingWceJson = whatsappIncomingMessageResource.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incomingWceJson))
                .andExpect(status().isOk());

        // Confirms that the AI successfully processed the prompt and handed off the text generation to your output service
        verify(whatsappService, timeout(5000)).sendTextMessage(eq("31612345678"), anyString());
    }
}
