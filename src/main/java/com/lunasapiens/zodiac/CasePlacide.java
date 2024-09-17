package com.lunasapiens.zodiac;

public class CasePlacide {

    private int numeroCasa;
    private String nomeCasa;
    private double gradi;

    private int gradiCasa;
    private int minutiCasa;

    private int numeroSegnoZodiacale;
    private String nomeSegnoZodiacale;


    public CasePlacide(int numeroCasa, String nomeCasa, double gradi, int gradiCasa, int minutiCasa, int numeroSegnoZodiacale, String nomeSegnoZodiacale) {
        this.numeroCasa = numeroCasa;
        this.nomeCasa = nomeCasa;
        this.gradi = gradi;
        this.gradiCasa = gradiCasa;
        this.minutiCasa = minutiCasa;
        this.numeroSegnoZodiacale = numeroSegnoZodiacale;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
    }


    public int getNumeroCasa() { return numeroCasa; }

    public String getNomeCasa() { return nomeCasa; }

    public double getGradi() {
        return gradi;
    }

    public int getGradiCasa() { return gradiCasa; }

    public int getMinutiCasa() { return minutiCasa; }

    public int getNumeroSegnoZodiacale() { return numeroSegnoZodiacale; }

    public String getNomeSegnoZodiacale() {
        return nomeSegnoZodiacale;
    }





    public String descrizioneCasa(){
        return "Casa " + nomeCasa + " in "+ nomeSegnoZodiacale + ". ";
    }


    public String descrizioneCasaGradi(){
        return "Casa " + nomeCasa + " in " + nomeSegnoZodiacale + " " + String.format("%.0f°", gradi);
    }

    public String descrizioneCasaGradiCasaMinutiCasa(){
        return "Casa " + nomeCasa + " in " + nomeSegnoZodiacale + ": " + String.format("%d°%02d’", gradiCasa, minutiCasa);
    }

    @Override
    public String toString() {
        return "Casa " + nomeCasa + " gradi: "+ gradi + " segno: "+ nomeSegnoZodiacale + " | ";
    }




}
