package com.lunasapiens.zodiac;

public class PianetiAspetti {

    private String nomePianeta;
    private int gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;


    public PianetiAspetti(String nomePianeta, int gradi, int minuti, int secondi, String nomeSegnoZodiacale) {
        this.nomePianeta = nomePianeta;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
    }

    public String getNomePianeta() {
        return nomePianeta;
    }

    public int getGradi() {
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

    @Override
    public String toString() {
        return nomePianeta + " in " + nomeSegnoZodiacale + ".";
    }
}
