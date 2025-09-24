package com.lunasapiens.service;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Traduttore custom per DJL che converte testo italiano in embedding.
 *
 * 🔹 Scopo della classe:
 *    Implementa l'interfaccia Translator di DJL per collegare il modello di
 *    embedding MiniLM multilingue/italiano al servizio TextEmbeddingService.
 *
 * 🔹 Funzionamento generale:
 *    1️⃣ Tokenizza il testo in input usando HuggingFaceTokenizer.
 *    2️⃣ Converte i token in NDArray, il formato richiesto dal modello DJL.
 *    3️⃣ Riceve l'embedding come NDArray dal modello e lo restituisce come float[].
 *
 * 🔹 A cosa serve:
 *    Permette a DJL di capire come trasformare testi italiani in vettori numerici,
 *    che saranno poi salvati nel database o usati per ricerche semantiche.
 */
public class HFMinilmItalianTranslator implements Translator<String, float[]> {


    /** Tokenizer HuggingFace per convertire testo in token numerici */
    private HuggingFaceTokenizer tokenizer;


    /**
     * Costruttore: inizializza il tokenizer locale.
     *
     * 🔹 Usa i file del modello locale per evitare problemi di memoria su Heroku.
     */
    public HFMinilmItalianTranslator() {
        try {
            // Path locale dei file del modello MiniLM
            Path modelPath = Paths.get( TextEmbeddingService.MODEL_PATH );
            tokenizer = HuggingFaceTokenizer.newInstance(modelPath);
        } catch (IOException e) {
            throw new RuntimeException("Errore caricando il tokenizer locale", e);
        }
    }


    /**
     * Prepara l'input del modello.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Tokenizza il testo in input.
     *    2️⃣ Converte i token in NDArray compatibile con DJL.
     *
     * @param ctx contesto del translator DJL
     * @param input testo da convertire in embedding
     * @return NDList pronto per essere passato al modello
     */
    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        long[] indices = tokenizer.encode(input).getIds(); // converte in token
        NDArray array = ctx.getNDManager().create(indices); // crea NDArray
        return new NDList(array); // ritorna come lista NDList
    }


    /**
     * Processa l'output del modello.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Riceve l'NDArray generato dal modello (embedding).
     *    2️⃣ Lo converte in array primitivo float[] da utilizzare in Java.
     *
     * @param ctx contesto del translator DJL
     * @param list lista di NDArray restituita dal modello
     * @return embedding come array di float
     */
    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        NDArray embedding = list.singletonOrThrow(); // prende l'unico NDArray
        return embedding.toFloatArray(); // converte in float[]
    }


    /**
     * Indica come il batch di input deve essere gestito.
     *
     * 🔹 Batchifier.STACK significa che più input vengono impilati lungo un asse,
     *    creando un batch compatibile con il modello.
     */
    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}
