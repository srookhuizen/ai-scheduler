package nl.tss.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.tss.ai_scheduler.dto.SendMessageRequest;
import nl.tss.ai_scheduler.dto.SendMessageRequestText;
import nl.tss.ai_scheduler.dto.SendMessageResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsappService {

    private final RestClient restClient;
    @Value("${whatsapp.api.base-url}")
    private String baseUrl;
    @Value("${whatsapp.api.access-token}")
    private String accessToken;

    public void sendTextMessage(String recipientMobile, String messageBody) {
        try {
            SendMessageRequestText textObject = new SendMessageRequestText();
            textObject.setBody(messageBody);
            textObject.setPreviewUrl(false);

            SendMessageRequest requestPayload = new SendMessageRequest();
            requestPayload.setMessagingProduct("whatsapp");
            requestPayload.setRecipientType("individual");
            requestPayload.setTo(recipientMobile);
            requestPayload.setType("text");
            requestPayload.setText(textObject);

            SendMessageResponse response = restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayload)
                    .retrieve()
                    .body(
                            SendMessageResponse.class);

            if (response != null && CollectionUtils.isNotEmpty(response.getMessages())) {
                log.info("Message processed successfully");
                log.info("Generated Message ID {}", response.getMessages().getFirst().getId());
            }

        } catch (Exception e) {
            log.error("Error while communicating to WCE Bridge", e);
        }
    }
}
