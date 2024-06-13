package com.lunasapiens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneratorImage {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorImage.class);

    public static final String folderOroscopoGiornalieroImmagine = "oroscopo_giornaliero/immagini/";

    public static void generateImage(String text, String fontName, int fontSize, Color textColor, String imagePathFileName) {
        // Creazione di un'immagine con dimensioni fisse
        int width = 700; int height = 400;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();

        // Abilita l'antialiasing per migliorare la qualità del testo
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Impostazione del colore di sfondo
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, width, height);

        // Impostazione del font e del colore del testo
        Font font = new Font(fontName, Font.BOLD, fontSize);
        graphics.setFont(font);
        graphics.setColor(textColor);

        // Ottenere FontMetrics
        FontMetrics fontMetrics = graphics.getFontMetrics();

        // Dividi il testo in righe basate sulla larghezza dell'immagine
        List<String> lines = splitTextIntoLines(text, fontMetrics, width);

        // Disegno del testo riga per riga
        int textY = (height - (lines.size() * fontMetrics.getHeight())) / 2 + fontMetrics.getAscent();
        for (String line : lines) {
            int textX = (width - fontMetrics.stringWidth(line)) / 2;
            graphics.drawString(line, textX, textY);
            textY += fontMetrics.getHeight();
        }

        // Salvataggio dell'immagine su disco
        try {
            Util.createDirectory( imagePathFileName );
            ImageIO.write(bufferedImage, "png", new File(imagePathFileName));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rilascio delle risorse
        graphics.dispose();
    }

    private static List<String> splitTextIntoLines(String text, FontMetrics fontMetrics, int width) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (fontMetrics.stringWidth(line.toString() + " " + word) <= width) {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        lines.add(line.toString());
        return lines;
    }



    public static void main(String[] args) {
        String text = "The stars shine for those who believe.";
        String fontName = "Arial";
        int fontSize = 30;
        Color textColor = Color.BLACK;
        generateImage(text, fontName, fontSize, textColor, "src/main/resources/generatedImage.png");
    }



}

