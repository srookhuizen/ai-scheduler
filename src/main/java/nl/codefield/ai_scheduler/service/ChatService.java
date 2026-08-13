package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.model.Customer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final CalendarService calendarService;
    private final BookingService bookingService;
    private final MessageChatMemoryAdvisor memoryAdvisor;
    private final SpeechService speechService;
    private final CustomerService customerService;
    private final ServiceService serviceService;

    @Value("classpath:/prompts/scheduler-booking.st")
    private Resource bookingPromptResource;
    @Value("classpath:/prompts/scheduler-registration.st")
    private Resource registrationPromptResource;

    public ChatClient.ChatClientRequestSpec chat(String message, String phoneNumber) {
        speechService.speak(message);

        ToolCallbackProvider calendarTools = MethodToolCallbackProvider.builder().toolObjects(calendarService).build();
        ToolCallbackProvider bookingTools = MethodToolCallbackProvider.builder().toolObjects(bookingService).build();
        ToolCallbackProvider serviceTools = MethodToolCallbackProvider.builder().toolObjects(serviceService).build();

        return chatClient.prompt()
                .system(getCompiledSystemPrompt(phoneNumber))
                .advisors(advisorSpec -> advisorSpec
                        .advisors(memoryAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, phoneNumber))
                .user(message)
                .tools(serviceTools.getToolCallbacks())
                .tools(calendarTools.getToolCallbacks())
                .tools(bookingTools.getToolCallbacks());
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
