package com.lunasapiens.manualjobs.SpeechToChunks.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@Service
public class AudioTranscriptionMultiThredService {

    private static final Logger logger = LoggerFactory.getLogger(AudioTranscriptionMultiThredService.class);

    private final ThreadLocal<Model> threadLocalModel = ThreadLocal.withInitial(() -> {
        try {
            return new Model( "src/test/resources/models/vosk-model-it-0.22" );
        } catch (Exception e) {
            throw new RuntimeException("Errore nel caricamento del modello Vosk", e);
        }
    });


    /**
     * Trascrive un file audio in testo.
     * Per scaricare il video da youtube usare il sito https://notube.link/it/youtube-app-241
     * Per convertire da MP3 a WAV usare software Audacity (free). Fare Apri mp3 e poi Esporta audio...
     * Il file deve essere WAV, Mono, 16kHz (oppure 16000Hz), PCM 16 bit - (per compatibilità con Vosk)
     */
    public String transcribeAudio(File audioFile) throws Exception {
        Model model = threadLocalModel.get();
        try (InputStream ais = new FileInputStream(audioFile);
             Recognizer recognizer = new Recognizer(model, 16000)) {

            byte[] buffer = new byte[4096];
            int nbytes;

            // Legge tutto l'audio senza stampare risultati parziali
            while ((nbytes = ais.read(buffer)) >= 0) {
                recognizer.acceptWaveForm(buffer, nbytes);
            }

            // JSON finale da Vosk
            String jsonResult = recognizer.getFinalResult();

            // Conversione charset (evita problemi con lettere accentate)
            String utf8Json = new String(jsonResult.getBytes("ISO-8859-1"), "UTF-8");

            // Parsing del JSON per ottenere solo il testo
            JSONObject json = new JSONObject(utf8Json);
            return json.optString("text", "");
        }
    }
}
