package nl.codefield.ai_scheduler.config;

import lombok.RequiredArgsConstructor;
import nl.codefield.ai_scheduler.chat_id.ChatIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ChatIdArgumentResolver chatIdArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(chatIdArgumentResolver);
    }
}
