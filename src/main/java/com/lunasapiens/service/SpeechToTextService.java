package com.lunasapiens.service;

import org.springframework.stereotype.Service;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class SpeechToTextService {

    private final Model model;

    public SpeechToTextService() throws Exception {
        // Imposta livello log
        LibVosk.setLogLevel(LogLevel.WARNINGS);

        // Carica il modello italiano grande
        // Assicurati che la cartella contenga "vosk-model-it-0.22"
        this.model = new Model("src/main/resources/models/vosk-model-it-0.22");
    }

    /**
     * Trascrive un file audio in testo.
     * Il file deve essere WAV, Mono, 16kHz (o 16000Hz), PCM, 16 bit - per compatibilità con Vosk
     */
    public String transcribeAudio(File audioFile) throws Exception {
        try (InputStream ais = new FileInputStream(audioFile);
             Recognizer recognizer = new Recognizer(model, 16000)) {

            byte[] buffer = new byte[4096];
            int nbytes;

            // Legge tutto l'audio senza stampare risultati parziali
            while ((nbytes = ais.read(buffer)) >= 0) {
                recognizer.acceptWaveForm(buffer, nbytes);
            }

            // Restituisce il testo completo già in UTF-8
            String result = recognizer.getFinalResult();
            return new String(result.getBytes("ISO-8859-1"), "UTF-8");


        }
    }
}
