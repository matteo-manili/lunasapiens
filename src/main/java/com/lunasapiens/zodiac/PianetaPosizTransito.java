package com.lunasapiens.zodiac;


public class PianetaPosizTransito {

    private int numeroPianeta;
    private String nomePianeta;
    private double gradi;
    private int minuti;
    private int secondi;

    private int numeroSegnoZodiacale;
    private String nomeSegnoZodiacale;
    private boolean retrogrado;

    private String nomeCasa;

    private String significatoPianetaSegno;


    public PianetaPosizTransito(int numeroPianeta, String nomePianeta, double gradi, int minuti, int secondi, int numeroSegnoZodiacale, String nomeSegnoZodiacale, boolean retrogrado,
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

    public PianetaPosizTransito() { }

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

    public String getNomeCasa() { return nomeCasa; }

    public void setNomeCasa(String nomeCasa) { this.nomeCasa = nomeCasa; }


    // ---- descrizioni ----

    public String descrizionePianeta() {
        return nomePianeta + " in " + nomeSegnoZodiacale + "";
    }

    public String descrizione_Pianeta_Retrogrado() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + (retrogrado ? " (Retrogrado)" : "") + ". ";
    }

    public String descrizione_Pianeta_Segno_Gradi_Retrogrado() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + ". ";
    }

    public String descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + " Casa "+nomeCasa;
    }

    public String descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + " "+significatoPianetaSegno+" ";
    }

    public String descrizione_Pianeta_Gradi_Retrogrado_Casa_SignificatoPianetaSegno() {
        // Usa String.format per rimuovere i decimali
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") +" Casa "+nomeCasa
                + " "+significatoPianetaSegno;
    }


    @Override
    public String toString() {
        return nomePianeta + " ("+gradi+"° "+minuti+"' "+secondi+"\") in " + nomeSegnoZodiacale + ". ";
    }


}
