package com.lunasapiens.zodiac;

public class PianetaPosizione {

    private int numeroPianeta;
    private String nomePianeta;
    private double gradi;
    private int minuti;
    private int secondi;

    private int numeroSegnoZodiacale;
    private String nomeSegnoZodiacale;
    private boolean retrogrado;

    private String significatoPianetaSegno;


    public PianetaPosizione(int numeroPianeta, String nomePianeta, double gradi, int minuti, int secondi, int numeroSegnoZodiacale, String nomeSegnoZodiacale, boolean retrogrado,
        String significatoPianetaSegno) {
        this.numeroPianeta = numeroPianeta;
        this.nomePianeta = nomePianeta;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.numeroSegnoZodiacale = numeroSegnoZodiacale;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
        this.retrogrado = retrogrado;
        this.significatoPianetaSegno = significatoPianetaSegno;
    }

    public int getNumeroPianeta() { return numeroPianeta; }

    public String getNomePianeta() { return nomePianeta;
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

    public int getNumeroSegnoZodiacale() { return numeroSegnoZodiacale; }

    public String getNomeSegnoZodiacale() {
        return nomeSegnoZodiacale;
    }

    public boolean isRetrogrado() { return retrogrado; }

    public String getSignificatoPianetaSegno() { return significatoPianetaSegno; }






    public String descrizionePianeta() {
        return nomePianeta + " in " + nomeSegnoZodiacale + ". ";
    }

    public String descrizionePianetaGradiRetrogrado() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + ". ";
    }

    public String descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + " "+significatoPianetaSegno+" ";
    }




    @Override
    public String toString() {
        return nomePianeta + " ("+gradi+"° "+minuti+"' "+secondi+"\") in " + nomeSegnoZodiacale + ". ";
    }


}
