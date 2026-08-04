package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.IncomingMessage;
import nl.codefield.ai_scheduler.dto.WebhookChange;
import nl.codefield.ai_scheduler.dto.WebhookEntry;
import nl.codefield.ai_scheduler.dto.WebhookPayload;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.service.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WhatsappWebhookController {

    private final WhatsappService apiService;
    private final ChatClient chatClient;
    private final CalendarService calendarService;
    private final BookingService bookingService;
    private final MessageChatMemoryAdvisor memoryAdvisor;
    private final SpeechService speechService;
    private final CustomerService customerService;

    @Value("classpath:/prompts/scheduler-booking.st")
    private Resource bookingPromptResource;
    @Value("classpath:/prompts/scheduler-registration.st")
    private Resource registrationPromptResource;

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
                                    sendResponse(senderNumber, messageText, senderNumber);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    private void sendResponse(String memoryId, String message, String phoneNumber) {
        speechService.speak(message);

        ToolCallbackProvider calendarTools = MethodToolCallbackProvider.builder().toolObjects(calendarService).build();
        ToolCallbackProvider bookingTools = MethodToolCallbackProvider.builder().toolObjects(bookingService).build();

        String responseMessage = chatClient.prompt()
                .system(getCompiledSystemPrompt(phoneNumber))
                .advisors(advisorSpec -> advisorSpec
                        .advisors(memoryAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, memoryId))
                .user(message)
                .tools(calendarTools.getToolCallbacks())
                .tools(bookingTools.getToolCallbacks())
                .call().chatResponse().getResult().getOutput().getText();

        apiService.sendTextMessage(phoneNumber, responseMessage);
    }

    private String getCompiledSystemPrompt(String phoneNumber) {
        String dynamicTimestamp = ZonedDateTime.now(ZoneId.of("Europe/Amsterdam"))
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' hh:mm a z"));

        Optional<Customer> optionalCustomer = customerService.findByPhoneNumber(phoneNumber);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            log.info("Customer found: {}. Loading booking template.", customer);

            SystemPromptTemplate template = new SystemPromptTemplate(bookingPromptResource);
            return template.createMessage(Map.of(
                    "currentDate", dynamicTimestamp,
                    "customerName", customer.getName(),
                    "phoneNumber", phoneNumber
            )).getText();
        } else {
            log.info("New customer detected. Loading registration template.");

            SystemPromptTemplate template = new SystemPromptTemplate(registrationPromptResource);
            return template.createMessage(Map.of(
                    "currentDate", dynamicTimestamp,
                    "phoneNumber", phoneNumber
            )).getText();
        }
    }
}
