package com.lunasapiens.zodiac;

public class PianetaPosizione {

    private String nomePianeta;
    private double gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;


    public PianetaPosizione(String nomePianeta, double gradi, int minuti, int secondi, String nomeSegnoZodiacale) {
        this.nomePianeta = nomePianeta;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
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


    public String descrizionePianeta() {
        return nomePianeta + " in " + nomeSegnoZodiacale + ". ";
    }

    public String descrizionePianetaGradi() {
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + gradi+"°. ";
    }

    @Override
    public String toString() {
        return nomePianeta + " ("+gradi+"° "+minuti+"' "+secondi+"\") in " + nomeSegnoZodiacale + ". ";
    }


}
