package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String text;
    private String phoneNumber;
    private String senderId;
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();
}
