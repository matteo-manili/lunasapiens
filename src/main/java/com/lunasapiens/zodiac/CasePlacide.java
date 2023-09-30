package com.lunasapiens.zodiac;

public class CasePlacide {

    private String nomeCasa;
    private int gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;


    public CasePlacide(String nomeCasa, int gradi, int minuti, int secondi, String nomeSegnoZodiacale) {
        this.nomeCasa = nomeCasa;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
    }

    public String getNomeCasa() {
        return nomeCasa;
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
        return "Casa " + nomeCasa + ": " + nomeSegnoZodiacale + ".";
    }
}
