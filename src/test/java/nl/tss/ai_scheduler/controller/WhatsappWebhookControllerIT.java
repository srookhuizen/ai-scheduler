package nl.tss.ai_scheduler.controller;

import nl.tss.ai_scheduler.service.CalendarService;
import nl.tss.ai_scheduler.service.WhatsappService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WhatsappWebhookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 4.1.0 Mockito overschrijvingen
    @MockitoBean
    private WhatsappService whatsappService;

    @MockitoBean
    private CalendarService calendarService;

    @MockitoBean
    private ChatModel chatModel;

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:whatsapp-incoming-message.json")
    private Resource whatsappIncomingMessageResource;

    // Voorkomt de NullPointerException door lege ChatOptions te serveren aan de ChatClient builder
    @BeforeEach
    void setUp() {
        when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());
    }

    @Test
    void testWebhookVerification_Success() throws Exception {
        // Test de GET-handshake die WCE en Meta gebruiken om je endpoint lokaal te valideren
        mockMvc.perform(get("/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "my-secret-wce-verify-token")
                        .param("hub.challenge", "123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string("123456789"));
    }

    @Test
    void testWebhookVerification_InvalidToken() throws Exception {
        mockMvc.perform(get("/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "WRONG_TOKEN")
                        .param("hub.challenge", "123456789"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testHandleIncomingMessage_TriggersAIAndSendsWhatsapp() throws Exception {
        String incomingWceJson = whatsappIncomingMessageResource.getContentAsString(StandardCharsets.UTF_8);

        ChatResponse mockChatResponse = Mockito.mock(ChatResponse.class);
        Generation mockGeneration = Mockito.mock(Generation.class);

        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("Afspraak is ingepland!"));

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incomingWceJson))
                .andExpect(status().isOk());

        verify(whatsappService).sendTextMessage(eq("31612345678"), eq("Afspraak is ingepland!"));
    }
}
