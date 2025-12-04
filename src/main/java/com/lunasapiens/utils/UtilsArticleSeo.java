package com.lunasapiens.utils;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

public class UtilsArticleSeo {



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
