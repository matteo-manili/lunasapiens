package com.lunasapiens;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageGenerator {


    public static void main(String[] args) {
        String text = "Hello, World!";
        String fontName = "Arial";
        int fontSize = 20;
        Color textColor = Color.BLUE;

        generateImage(text, fontName, fontSize, textColor, "src/main/resources/generatedImage.png");
    }

    public static void generateImage(String text, String fontName, int fontSize, Color textColor, String outputPath) {
        // Creazione di un'immagine con dimensioni fisse
        int width = 300;
        int height = 100;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();

        // Impostazione del colore di sfondo
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // Impostazione del font e del colore del testo
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        graphics.setFont(font);
        graphics.setColor(textColor);

        // Disegno della stringa al centro dell'immagine
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textX = (width - fontMetrics.stringWidth(text)) / 2;
        int textY = (height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        graphics.drawString(text, textX, textY);

        // Salvataggio dell'immagine su disco
        try {
            ImageIO.write(bufferedImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rilascio delle risorse
        graphics.dispose();
    }
}

