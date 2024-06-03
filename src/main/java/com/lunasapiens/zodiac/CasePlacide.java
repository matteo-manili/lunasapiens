package com.lunasapiens.zodiac;

public class CasePlacide {

    private String nomeCasa;
    private double gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;


    public CasePlacide(String nomeCasa, double gradi, int minuti, int secondi, String nomeSegnoZodiacale) {
        this.nomeCasa = nomeCasa;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
    }

    public String getNomeCasa() {
        return nomeCasa;
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


    public String descrizioneCasa(){
        return "Casa " + nomeCasa + " in "+ nomeSegnoZodiacale + ". ";
    }

    public String descrizioneCasaGradi(){
        return "Casa " + nomeCasa + " in "+ nomeSegnoZodiacale + " " + gradi+"°. ";
    }

    @Override
    public String toString() {
        return "Casa " + nomeCasa + " gradi: "+ gradi + " segno: "+ nomeSegnoZodiacale + " | ";
    }
}
