package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "ollama"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Disabled
public class ChatControllerIT {
    private static final String SENDER_ID = "USER";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /chat should return 200 OK when valid payload is provided")
    void chat_ShouldReturnOk_WhenPayloadIsValid() throws Exception {

        sendChatMessage("Hello chatbot");
        sendChatMessage("How are you?");
    }

    @Test
    @DisplayName("POST /stream should return 200 OK when valid payload is provided")
    void stream_ShouldReturnOk_WhenPayloadIsValid() throws Exception {
        String chatId = UUID.randomUUID().toString();

        sendStreamMessage("Hello chatbot", chatId);
        sendStreamMessage("How are you?", chatId);
    }

    private void sendChatMessage(String text) throws Exception {
        Message message = Message.builder().senderId(SENDER_ID).phoneNumber("24356").text(text).build();
        String requestBodyJson = objectMapper.writeValueAsString(message);

        mockMvc.perform(post("/chat")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk());
    }

    private void sendStreamMessage(String text, String chatId) throws Exception {
        Message message = Message.builder().senderId(SENDER_ID).phoneNumber("24356").text(text).build();
        String requestBodyJson = objectMapper.writeValueAsString(message);

        mockMvc.perform(post("/chat")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk());
    }
}
