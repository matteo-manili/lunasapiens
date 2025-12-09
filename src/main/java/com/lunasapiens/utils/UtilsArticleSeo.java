package com.lunasapiens.utils;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.text.Normalizer;

public class UtilsArticleSeo {




    /**
     * Genera uno slug SEO-friendly a partire da un titolo.
     *
     * Il metodo:
     * 1. Trasforma tutti i caratteri in minuscolo.
     * 2. Rimuove gli accenti dalle lettere (es. "à" → "a").
     * 3. Rimuove tutti i simboli non alfanumerici (eccetto spazi).
     * 4. Sostituisce gli spazi con trattini (-).
     * 5. Opzionalmente può limitare la lunghezza massima dello slug.
     *
     * Esempio:
     * Input:  "Università degli Studi di Milano!"
     * Output: "universita-degli-studi-di-milano"
     *
     * @param input Il titolo da cui generare lo slug
     * @return Lo slug SEO-friendly derivato dal titolo
     */
    public static String toSlug(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")      // rimuove accenti
                .replaceAll("[^a-zA-Z0-9\\s]", "")   // rimuove simboli
                .trim()
                .replaceAll("\\s+", "-")             // spazi -> trattini
                .toLowerCase();
    }




    /**
     * Pulisce un testo generato da LLM rimuovendo eventuali virgolette iniziali e finali.
     * Gestisce sia virgolette standard (") che tipografiche (“”).
     *
     * @param title titolo generato dal modello
     * @return titolo senza virgolette ai bordi
     */
    public static String cleanGeneratedText(String title) {
        if (title == null || title.isBlank()) {
            return title; // restituisce null o stringa vuota così com'è
        }
        title = title.strip(); // rimuove spazi iniziali e finali

        // rimuove virgolette iniziali e finali
        if ((title.startsWith("\"") && title.endsWith("\"")) ||
                (title.startsWith("“") && title.endsWith("”"))) {
            title = title.substring(1, title.length() - 1).strip();
        }

        return title;
    }




    /**
     * Pulisce l'HTML:
     * - Rimuove i tag
     * - Decodifica le entità HTML (&nbsp;, &egrave; → è, ecc.)
     * - Normalizza spazi multipli
     */
    public static String cleanText(String html) {
        if (html == null) return "";

        // 1. Pulisce HTML e decodifica entità
        String text = Jsoup.parse(html, "", Parser.htmlParser()).text();

        // 2. Normalizza spazi
        text = text.replaceAll("\\s+", " ").trim();

        // 3. Rimuove virgolette tipografiche e caratteri di controllo strani
        text = text.replaceAll("^[“”\"'«»]+", ""); // solo all'inizio
        text = text.replaceAll("[“”\"'«»]+$", ""); // solo alla fine

        return text;
    }


}
