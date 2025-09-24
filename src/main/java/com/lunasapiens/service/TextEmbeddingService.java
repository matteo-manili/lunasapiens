package com.lunasapiens.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.lunasapiens.Utils;
import com.lunasapiens.repository.ArticleContentRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Servizio per il calcolo degli embedding testuali.
 *
 * 🔹 Scopo della classe:
 *    Permette di trasformare testi in vettori numerici (embedding) utilizzabili
 *    per ricerche semantiche e similarity search.
 *
 * 🔹 Funziona tramite DJL (Deep Java Library) con modelli PyTorch locali o
 *    scaricati da Hugging Face.
 */
@Service
public class TextEmbeddingService {



    /** Modello DJL caricato dal ModelZoo */
    public final ZooModel<String, float[]> model;

    /** Predictor DJL per calcolare gli embedding dal testo */
    public final Predictor<String, float[]> predictor;

    /** Percorso locale del modello ottimizzato per Heroku */
    public static final String MODEL_PATH = "src/main/resources/models/multi-qa-MiniLM-L6-cos-v1-djl";


    /**
     * Costruttore: carica il modello e inizializza il predictor.
     *
     * @param repository repository degli articoli (non usato direttamente qui)
     */
    public TextEmbeddingService(ArticleContentRepository repository) {

        System.setProperty("ai.djl.logging.level", "DEBUG"); // ATTIVA LOG DEBUG

        try {

            // FUNZIONA CON FILE (multi-qa-MiniLM-L6-cos-v1.pt) IN LOCALE MA PESANTE PER HEROKU FALLISCE IL DEPLOY, PRENDE TROPPA MEMORIA - ricerche semantiche ottime !!!
            // ho ridotto il peso del modello, adesso su heroku funziona
            // modello da 384 - creato custom con python dal "pytorch_model.bin" ho creto il "multi-qa-MiniLM-L6-cos-v1.pt"
            // per farlo vedere cartella C:\intellij_work\modello_minilm
            // Definisce i criteri per caricare il modello di embedding
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")
                    .optModelPath(Paths.get( MODEL_PATH ))
                    .optTranslator(new ItalianTextEmbeddingTranslator()) // Traduttore custom per testi italiani
                    .build();


            /*
            // FUNZIONA IN LOCALE MA PESANTE PER HEROKU, PRENDE TROPPA MEMORIA E DA ERRORE: Process running mem=1029M(201.0%) - Error R15 (Memory quota vastly exceeded)
            // - Stopping process with SIGKILL - RICERCHE SEMANTICHE QUASI BUONE
            // modello da 768
            // 1️⃣ Definisci criteri per caricare il modello di embedding da Hugging Face online, utilizza il modello di default, solo inglese
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch") // Assicura uso PyTorch
                    .build();
             */


            // modello da 384 OK
            /*
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")

                    // FUNZIONA IN LOCALE MA PESANTE PER HEROKU FALLISCE IL DEPLOY, PRENDE TROPPA MEMORIA - RICERCHE SEMANTICHE QUASI BUONE
                    //.optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")

                    // FUNZIONA IN LOCALE E SU HEROKU - RICERCHE DANNO NON BUONO RISULTATI - OK !!!!
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2") // FUNZIONA GIRA SU HEROKU - RICERCHE SEMANTICHE PESSIME

                    .build();

             */


            // Carica il modello dal ModelZoo (può scaricare online se necessario)
            model = ModelZoo.loadModel(criteria);

            // Crea il predictor per calcolare embedding dai testi
            predictor = model.newPredictor();

        } catch (IOException | ModelException e) {
            throw new RuntimeException("Errore caricando il modello di embedding online", e);
        }
    }



    /**
     * Calcola l'embedding di un testo pulito.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Pulisce il testo rimuovendo caratteri speciali o formattazioni inutili.
     *    2️⃣ Utilizza il predictor per ottenere l'embedding.
     *    3️⃣ Converte l'array primitivo float[] in Float[] per compatibilità con DB.
     *
     * @param content testo di input
     * @return embedding come array di Float
     */
    public Float[] computeCleanEmbedding(String content)  {
        try {
            String text = Utils.cleanText(content); // Pulizia del testo
            System.out.println(text);
            return Utils.toFloatObjectArray( predictor.predict( text ) ); // Calcolo embedding
        } catch (TranslateException e) {
            e.printStackTrace();
            return null;
        }
    }







}
