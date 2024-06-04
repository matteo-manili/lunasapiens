package com.lunasapiens.zodiac;

public class PianetaPosizione {

    private String nomePianeta;
    private double gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;
    private boolean retrogrado;


    public PianetaPosizione(String nomePianeta, double gradi, int minuti, int secondi, String nomeSegnoZodiacale, boolean retrogrado) {
        this.nomePianeta = nomePianeta;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
        this.retrogrado = retrogrado;
    }

    public String getNomePianeta() {
        return nomePianeta;
    }

    public double getGradi() {
        return gradi;
    }

    public int getMinuti() {
        return minuti;
    }

    public int getSecondi() {
        return secondi;
    }

    public String getNomeSegnoZodiacale() {
        return nomeSegnoZodiacale;
    }

    public boolean isRetrogrado() { return retrogrado; }



    public String descrizionePianeta() {
        return nomePianeta + " in " + nomeSegnoZodiacale + ". ";
    }

    public String descrizionePianetaGradiRetrogrado() {

        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + ". ";
    }

    @Override
    public String toString() {
        return nomePianeta + " ("+gradi+"° "+minuti+"' "+secondi+"\") in " + nomeSegnoZodiacale + ". ";
    }


}
