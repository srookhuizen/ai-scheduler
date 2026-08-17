package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.IncomingMessage;
import nl.codefield.ai_scheduler.dto.WebhookChange;
import nl.codefield.ai_scheduler.dto.WebhookEntry;
import nl.codefield.ai_scheduler.dto.WebhookPayload;
import nl.codefield.ai_scheduler.service.ChatService;
import nl.codefield.ai_scheduler.service.WhatsappService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WhatsappChatController {

    private final WhatsappService apiService;
    private final ChatService chatService;

    @Value("${whatsapp.api.verify-token}")
    private String configuredVerifyToken;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && configuredVerifyToken.equals(token)) {
            log.info("Webhook validated successfully!");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
    }

    @PostMapping
    public ResponseEntity<Void> handleIncomingMessage(@RequestBody WebhookPayload payload) {
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

                                if (StringUtils.hasText(messageText)) {
                                    log.info("Incoming message from {}: {}", senderNumber, messageText);
                                    sendResponse(messageText, senderNumber);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    private void sendResponse(String message, String phoneNumber) {

        String responseMessage = chatService.chat(message, phoneNumber)
                .call().chatResponse().getResult().getOutput().getText();

        apiService.sendTextMessage(phoneNumber, responseMessage);
    }
}
