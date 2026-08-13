package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message chat(@RequestBody Message message) {
        log.info("Received request for message: {}", message);
        return Optional.ofNullable(
                        chatService.chat(message.getText(), message.getPhoneNumber())
                                .call()
                                .content()
                )
                .map(text -> Message.builder()
                        .text(text)
                        .phoneNumber(message.getPhoneNumber())
                        .senderId("AGENT")
                        .build()).orElse(null);
    }


    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> stream(@RequestBody Message message) {
        log.info("Received request to stream message: {}", message);
        return chatService.chat(message.getText(), message.getPhoneNumber())
                .stream()
                .content()
                .map(text -> Message.builder()
                        .text(text)
                        .phoneNumber(message.getPhoneNumber())
                        .senderId("AGENT")
                        .build());
    }
}
