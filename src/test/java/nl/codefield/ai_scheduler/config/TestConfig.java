package nl.codefield.ai_scheduler.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Value("${evaluation.ollama.model}")
    private String evaluationModelName;

    @Value("${evaluation.ollama.temperature}")
    private Double evaluationTemperature;

    @Bean(name = "evaluationChatClient")
    public ChatClient evaluationChatClient(OllamaApi ollamaApi) {
        // The injected ollamaApi instance already contains the properties timeouts
        OllamaChatModel evaluationModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .options(OllamaChatOptions.builder()
                        .model(evaluationModelName)
                        .temperature(evaluationTemperature)
                        .build())
                .build();

        return ChatClient.builder(evaluationModel).build();
    }

    @Bean
    @Primary
    public ChatClient testPrimaryChatClient(
            ObjectProvider<ChatClient> chatClientProvider,
            @Qualifier("evaluationChatClient") ObjectProvider<ChatClient> evaluationProvider) {

        // Find the production client by filtering out the evaluation client
        ChatClient evaluationClient = evaluationProvider.getIfAvailable();

        return chatClientProvider.orderedStream()
                .filter(client -> client != evaluationClient)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No production ChatClient found active in profiles!"));
    }
}
