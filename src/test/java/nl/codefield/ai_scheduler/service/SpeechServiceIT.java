package nl.codefield.ai_scheduler.service;

import nl.codefield.ai_scheduler.config.TestConfig;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "google"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({
        //DockerTestConfig.class
        TestConfig.class
})
@Ignore
public class SpeechServiceIT {
    @Autowired
    private SpeechService speechService;

    @Test
    void testSpeech() {
        speechService.speak("Hello World!");
    }
}
