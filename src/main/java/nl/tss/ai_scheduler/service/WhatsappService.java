package nl.tss.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import nl.tss.ai_scheduler.dto.SendMessageRequest;
import nl.tss.ai_scheduler.dto.SendMessageRequestText;
import nl.tss.ai_scheduler.dto.SendMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
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
          .uri(baseUrl) // Dit wijst naar http://localhost:3001/send-to-emulator
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + accessToken)
          .body(requestPayload)
          .retrieve()
          .body(
              SendMessageResponse.class);

      if (response != null) {
        System.out.println("Bericht succesvol verwerkt door WCE Bridge!");
        System.out.println("Gegenereerd Message ID: " + response.getMessages().get(0).getId());
      }

    } catch (Exception e) {
      System.err.println("Error met Spring RestClient richting WCE Bridge: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
