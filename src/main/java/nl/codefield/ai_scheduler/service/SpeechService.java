package nl.codefield.ai_scheduler.service;

import javazoom.jl.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeechService {

    private final TextToSpeechModel speechModel;

    public void speak(String text) {

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text);

        try {
            TextToSpeechResponse response = speechModel.call(prompt);
            byte[] audioBytes = response.getResult().getOutput();

            //Path outputPath = Path.of(System.getProperty("user.dir") + "/audio-response.mp3");
            //Files.write(outputPath, audioBytes);

            Player player = new Player(new BufferedInputStream(new ByteArrayInputStream(audioBytes)));
            player.play();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
