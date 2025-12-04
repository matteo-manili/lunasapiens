package com.lunasapiens;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

public class UtilsArticleSeo {



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


    /**
     * Controlla se il testo è solo un URL (con eventuali spazi o newline attorno)
     */
    private static boolean isOnlyUrl(String text) {
        if (text == null) return false;
        String trimmed = text.trim();
        // Rimuove eventuali punti finali o caratteri extra
        trimmed = trimmed.replaceAll("[.,;!?]+$", "");
        return trimmed.matches("https?://\\S+");
    }



}
