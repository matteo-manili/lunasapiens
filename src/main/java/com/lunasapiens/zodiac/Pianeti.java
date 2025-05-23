package com.lunasapiens.zodiac;


import com.lunasapiens.Constants;

public class Pianeti {

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


    public Pianeti(int numeroPianeta, String nomePianeta, double gradi, int minuti, int secondi, int numeroSegnoZodiacale, String nomeSegnoZodiacale, boolean retrogrado,
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

    public Pianeti() { }

    public int getNumeroPianeta() { return numeroPianeta; }

    public String getNomePianeta() { return nomePianeta;
    }
    public double getGradi() { return gradi; }

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

    public String descrizionePianetaSegno() {
        return nomePianeta + " in " + nomeSegnoZodiacale + "";
    }

    public String descrizionePianetaTipoPianetaSegno() {
        return nomePianeta + " (pianeta "+ Constants.Pianeti.fromNumero( numeroPianeta ).getTipoPianeta().getName() +")" + " in " + nomeSegnoZodiacale + "";
    }

    public String descrizione_Pianeta_Retrogrado() {
        return nomePianeta + (retrogrado ? " (Retrogrado)" : "");
    }

    public String descrizione_Pianeta_Segno_Gradi_Retrogrado() {
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + minuti + "°" + secondi + "’" + (retrogrado ? " (Retrogrado)" : "") + ". ";
    }

    public String descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() {
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + minuti + "°" + secondi + "’" + (retrogrado ? " (Retrogrado)" : "") + " in Casa "+nomeCasa+":";
    }

    public String descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno() {
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + minuti + "°" + secondi + "’" + (retrogrado ? " (Retrogrado)" : "") + " "+significatoPianetaSegno;
   }

    /**
     * deprecato perché i grandi si devono sempre intendere in minuti-secondi. E inoltre questi gradi provengono
     * da BuildInfoAstrologiaSwiss che è stata sostituita da BuildInfoAstrologiaAstroSeek
     */
    @Deprecated
    public String descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno_OROSCOPO() {
        return nomePianeta + " in " + nomeSegnoZodiacale + " " + String.format("%.0f", gradi) + "°" + (retrogrado ? " (Retrogrado)" : "") + " "+significatoPianetaSegno;
    }



    @Override
    public String toString() {
        return nomePianeta + " ("+gradi+"° "+minuti+"' "+secondi+"\") in " + nomeSegnoZodiacale + ". ";
    }


}
