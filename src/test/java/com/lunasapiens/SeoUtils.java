package com.lunasapiens;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.text.Normalizer;

public class SeoUtils {

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



    /**
     * Genera un titolo SEO-friendly
     */
    public static String generateTitle(String content, Long articleId) {
        String text = cleanText(content);
        if (text.isBlank() || isOnlyUrl(text)) {
            return "Articolo di approfondimento " + articleId;
        }
        String[] sentences = text.split("\\.\\s+");
        for (String sentence : sentences) {
            if (!isOnlyUrl(sentence)) {
                String title = sentence.trim();
                if (title.length() > 70) title = title.substring(0, 67) + "...";
                return title;
            }
        }
        // fallback
        return "Articolo di approfondimento " + articleId;
    }


    /**
     * Genera una descrizione SEO-friendly
     */
    public static String generateDescription(String content, Long articleId) {
        String text = cleanText(content);
        if (text.isBlank() || isOnlyUrl(text)) {
            return "Approfondimento disponibile nell'articolo " + articleId + ".";
        }
        String[] sentences = text.split("\\.\\s+");
        StringBuilder description = new StringBuilder();
        int count = 0;
        for (String sentence : sentences) {
            if (sentence.isBlank()) continue;
            if (!description.isEmpty()) description.append(". ");
            description.append(sentence);
            count++;
            if (count >= 2) break;
        }
        String desc = description.toString();
        if (desc.length() > 160) desc = desc.substring(0, 157) + "...";
        return desc;
    }

    /**
     * Genera uno slug SEO-friendly
     */
    public static String generateSlug(String content, Long articleId) {
        String title = generateTitle(content, articleId);
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // rimuove accenti
        String lower = normalized.toLowerCase();
        String slug = lower.replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", ""); // rimuove - iniziale/finale
        if (slug.isBlank() || slug.startsWith("http")) {
            slug = "articolo-" + articleId;
        }
        return slug;
    }
}
