package nl.tss.ai_scheduler.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

  @Value("classpath:/prompts/scheduler-system-prompt.st")
  private Resource systemPromptResource;

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }

  @Bean
  public String systemMessage() {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    SystemPromptTemplate template = new SystemPromptTemplate(systemPromptResource);
    Message systemMessage = template.createMessage(Map.of(
        "currentDate", today,
        "openingTime", "9:00",
        "closingTime", "17:00"
    ));
    return systemMessage.getText();
  }

  @Bean
  public MessageChatMemoryAdvisor memoryAdvisor() {
    InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();

    MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(memoryRepository)
        .maxMessages(100)
        .build();

    return MessageChatMemoryAdvisor.builder(chatMemory)
        .build();
  }
}
