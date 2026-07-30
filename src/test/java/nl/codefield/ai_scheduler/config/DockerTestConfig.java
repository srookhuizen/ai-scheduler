package nl.codefield.ai_scheduler.config;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ollama.OllamaContainer;

@TestConfiguration(proxyBeanMethods = false)
public class DockerTestConfig {

    @Bean
    @ServiceConnection
    OllamaContainer ollamaContainer() {
        return new OllamaContainer("ollama/ollama:latest");
    }

    @Bean
    ApplicationListener<ApplicationStartedEvent> ollamaModelPuller(OllamaContainer ollamaContainer) {
        return event -> {
            try {
                // Manually execute the pull command inside the running container
                ollamaContainer.execInContainer("ollama", "pull", "qwen2.5:3b");
            } catch (Exception e) {
                throw new RuntimeException("Failed to pull Qwen model inside Ollama container", e);
            }
        };
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
