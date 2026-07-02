package nl.tss.ai_scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.tss.ai_scheduler.dto.IncomingMessage;
import nl.tss.ai_scheduler.dto.WebhookChange;
import nl.tss.ai_scheduler.dto.WebhookEntry;
import nl.tss.ai_scheduler.dto.WebhookPayload;
import nl.tss.ai_scheduler.service.CalendarService;
import nl.tss.ai_scheduler.service.WhatsappService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WhatsappWebhookController {

    private final WhatsappService apiService;
    private final ChatClient chatClient;
    private final CalendarService calendarService;
    private final String systemMessage;
    private final MessageChatMemoryAdvisor memoryAdvisor;

    @Value("${whatsapp.api.verify-token}")
    private String configuredVerifyToken;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && configuredVerifyToken.equals(token)) {
            System.out.println("Webhook validated successfully!");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
    }

    @PostMapping
    public ResponseEntity<Void> handleIncomingMessage(@RequestBody WebhookPayload payload, HttpSession session) {
        log.info("Incoming message received: {}", payload);

        if (payload != null && payload.getEntry() != null) {
            for (WebhookEntry entry : payload.getEntry()) {
                if (entry.getChanges() != null) {
                    for (WebhookChange change : entry.getChanges()) {

                        if (change.getValue() != null && change.getValue().getMessages() != null) {
                            List<IncomingMessage> messages = change.getValue().getMessages();

                            for (IncomingMessage msg : messages) {
                                String senderNumber = msg.getFrom();
                                String messageText = (msg.getText() != null) ? msg.getText().getBody() : "";

                                log.info("Incoming message from {}: {}", senderNumber, messageText);
                                if (StringUtils.hasText(messageText)) {
                                    sendResponse(session.getId(), messageText, senderNumber);
                                }
                            }
                        }

                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    private void sendResponse(String sessionId, String message, String senderNumber) {
        String responseMessage = Objects.requireNonNull(Objects.requireNonNull(chatClient.prompt()
                .system(systemMessage)
                .advisors(advisorSpec -> advisorSpec
                        .advisors(memoryAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(message)
                .tools(calendarService)
                .call().chatResponse()).getResult()).getOutput().getText();
        apiService.sendTextMessage(senderNumber, responseMessage);
    }
}
