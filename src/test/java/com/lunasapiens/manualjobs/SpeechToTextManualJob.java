package com.lunasapiens.manualjobs;

import com.lunasapiens.service.SpeechToTextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpeechToTextManualJob {

    @Autowired
    private SpeechToTextService speechToTextService;

    @Test
    void testTranscribeAudioFile() throws Exception {

        // Percorso del file audio nella cartella resources
        //Formato: WAV
        //Bit depth: 16 bit
        //Canali: Mono
        //Sample rate: 16 kHz o 16000 Hz
        File audioFile = new File("src/main/resources/models/vosk-model-it-0.22/AAA-file-audio/sample.wav");

        assertTrue(audioFile.exists(), "Il file audio deve esistere: " + audioFile.getAbsolutePath());

        // Trascrizione del file audio
        String transcription = speechToTextService.transcribeAudio(audioFile);

        // Stampa il testo trascritto
        System.out.println("Testo trascritto:");
        System.out.println(transcription);

        // Controllo base: la trascrizione non deve essere vuota
        assertNotNull(transcription, "La trascrizione non dovrebbe essere null");
        assertFalse(transcription.trim().isEmpty(), "La trascrizione non dovrebbe essere vuota");
    }
}
