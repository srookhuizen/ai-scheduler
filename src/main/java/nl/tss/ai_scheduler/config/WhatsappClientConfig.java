package nl.tss.ai_scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WhatsappClientConfig {

  @Bean
  public RestClient whatsappRestClient() {
    // Gebruikt de standaard ingebouwde Java netwerk-stack die standaard HTTP/1.1 afdwingt
    var factory = new SimpleClientHttpRequestFactory();

    return RestClient.builder()
        .requestFactory(factory)
        .build();
  }
}
