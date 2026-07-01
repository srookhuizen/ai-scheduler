package nl.tss.ai_scheduler.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import nl.tss.ai_scheduler.service.CalendarService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatClient chatClient;
  private final CalendarService calendarService;
  private final String systemMessage;
  private final MessageChatMemoryAdvisor memoryAdvisor;

  @GetMapping("/schedule")
  public String schedule(
      @RequestParam(
          value = "message",
          defaultValue = "I want to make an appointment today between 16:00 and 16:30.")
      String message,
      HttpSession session) {

    return Objects.requireNonNull(Objects.requireNonNull(chatClient.prompt()
        .system(systemMessage)
        .advisors(advisorSpec -> advisorSpec
            .advisors(memoryAdvisor)
            .param(ChatMemory.CONVERSATION_ID, session.getId()))
        .user(message)
        .tools(calendarService)
        .call().chatResponse()).getResult()).getOutput().getText();
  }
}
