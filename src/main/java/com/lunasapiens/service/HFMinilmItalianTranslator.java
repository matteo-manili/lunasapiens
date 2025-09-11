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

public class HFMinilmItalianTranslator implements Translator<String, float[]> {

    private HuggingFaceTokenizer tokenizer;

    public HFMinilmItalianTranslator() {
        try {
            // Usa Path locale per i file del modello
            Path modelPath = Paths.get( ArticleEmbeddingService.MODEL_PATH );
            tokenizer = HuggingFaceTokenizer.newInstance(modelPath);
        } catch (IOException e) {
            throw new RuntimeException("Errore caricando il tokenizer locale", e);
        }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        // Tokenizza la stringa in input
        long[] indices = tokenizer.encode(input).getIds();
        NDArray array = ctx.getNDManager().create(indices);
        return new NDList(array);
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        // Prendi l'embedding dal modello e convertilo in float[]
        NDArray embedding = list.singletonOrThrow();
        return embedding.toFloatArray();
    }

    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}
