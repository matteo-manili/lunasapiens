package com.lunasapiens;


import jakarta.xml.bind.DatatypeConverter;

import java.io.*;


public class BinarioToImmagine {
    public static void main(String[] args) {
        // Percorsi del file di input (contenente la stringa esadecimale) e di output (dove salvare l'immagine)
        String fileInput = "C:\\intellij_work\\binario_1.txt";  // File che contiene il codice esadecimale
        String fileOutput = "C:\\intellij_work\\immagine.jpeg"; // File di output

        try {
            // Leggi il contenuto del file di testo
            String content = leggiFile(fileInput);

            // Pulisci la stringa esadecimale, rimuovendo prefissi non validi (come "x")
            content = content.replaceAll("[^0-9a-fA-F]", "");

            // Se la lunghezza della stringa è dispari, aggiungi uno "0" alla fine
            if (content.length() % 2 != 0) {
                content += "0"; // Aggiungi uno zero per rendere la lunghezza pari
            }

            // Converte la stringa esadecimale in byte
            byte[] datiImmagine = hexStringToByteArray(content);

            // Scrivi i byte nel file di output
            scriviImmagine(datiImmagine, fileOutput);

            System.out.println("Immagine salvata con successo in: " + fileOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo per leggere il contenuto del file di testo
    public static String leggiFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);  // Aggiungi ogni linea al contenuto
        }
        reader.close();
        return sb.toString();
    }

    // Metodo per convertire la stringa esadecimale in byte
    public static byte[] hexStringToByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    // Metodo per scrivere l'immagine su file
    public static void scriviImmagine(byte[] dati, String outputPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputPath);
        fos.write(dati);
        fos.close();
    }
}
