package nl.tss.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import nl.tss.ai_scheduler.service.WhatsappService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WhatsappWebhookController {

  private final WhatsappService apiService;
  @Value("${whatsapp.api.verify-token}")
  private String configuredVerifyToken;

  // Handshake Verification for the Cloud API / WCE Emulator
  @GetMapping
  public ResponseEntity<String> verifyWebhook(
      @RequestParam("hub.mode") String mode,
      @RequestParam("hub.verify_token") String token,
      @RequestParam("hub.challenge") String challenge) {

    if ("subscribe".equals(mode) && configuredVerifyToken.equals(token)) {
      System.out.println("Webhook validated successfully!");
      return ResponseEntity.ok(challenge);
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
  }

  // Handles incoming simulated or live messages
  @PostMapping
  public ResponseEntity<Void> handleIncomingMessage(@RequestBody String rawJsonPayload) {
    System.out.println("Received incoming payload:");
    System.out.println(rawJsonPayload);

    // Example Echo Bot logic:
    // In a real app, parse the JSON payload to extract the sender's number and text.
    // For testing, we mock an echo reply back to a hardcoded target test number:
    apiService.sendTextMessage("31612345678", "Echo reply: Received your message!");

    return ResponseEntity.ok().build();
  }
}
