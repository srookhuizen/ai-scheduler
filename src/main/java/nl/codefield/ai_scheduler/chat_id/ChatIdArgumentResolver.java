package nl.codefield.ai_scheduler.chat_id;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable; // Ensure this import is present
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class ChatIdArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String HEADER_NAME = "X-Correlation-ID";
    private static final String MDC_KEY = "chatId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ChatId.class);
    }

    @Override
    @Nullable
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        String chatId = null;

        if (request != null) {
            chatId = request.getHeader(HEADER_NAME);
        }

        ChatId ann = parameter.getParameterAnnotation(ChatId.class);
        boolean isRequired = ann != null && ann.required();

        if (chatId == null || chatId.isBlank()) {
            if (isRequired) {
                chatId = UUID.randomUUID().toString();
            } else {
                return null;
            }
        }

        MDC.put(MDC_KEY, chatId);

        return chatId;
    }
}
