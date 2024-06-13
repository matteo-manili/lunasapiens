package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SegnoZodiacale {

    @Autowired
    private AppConfig appConfig;

    private int numeroSegnoZodiacale;
    private String nomeSegnoZodiacale;
    private String descrizione;

    private String genere;
    private String natura;
    private String elemento;
    private String caratteristica;

    private int[] pianetaSignoreDelSegno;
    private int[] pianetainEsaltazione;
    private int[] pianetainEsilio;
    private int[] pianetainCaduta;



    public void SegnoZodiacale(int numeroSegno) {
        switch (numeroSegno) {
            case 0: // ariete
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(0);
                this.elemento = Constants.segnoZodiacaleElemento.get(0);
                this.caratteristica = "Impulsività";

                this.pianetaSignoreDelSegno = new int[]{ 4 };;
                this.pianetainEsaltazione = new int[]{ 0 };;
                this.pianetainEsilio = new int[]{ 3 };;
                this.pianetainCaduta = new int[]{ 6 };;
                break;
            case 1: // toro
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(1);
                this.elemento = Constants.segnoZodiacaleElemento.get(1);
                this.caratteristica = "Sforzo";

                this.pianetaSignoreDelSegno = new int[]{ 3 };;
                this.pianetainEsaltazione = new int[]{ 1 };;
                this.pianetainEsilio = new int[]{ 4 };;
                this.pianetainCaduta = new int[]{ 7 };;
                break;
            case 2: // gemelli
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(2);
                this.elemento = Constants.segnoZodiacaleElemento.get(2);
                this.caratteristica = "Dualità";

                this.pianetaSignoreDelSegno = new int[]{ 2 };;
                this.pianetainEsaltazione = new int[]{ 9 };;
                this.pianetainEsilio = new int[]{ 5 };;
                this.pianetainCaduta = null;;
                break;
            case 3: // cancro
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(0);
                this.elemento = Constants.segnoZodiacaleElemento.get(3);
                this.caratteristica = "Passività";

                this.pianetaSignoreDelSegno = new int[]{ 1 };;
                this.pianetainEsaltazione = new int[]{ 5 };;
                this.pianetainEsilio = new int[]{ 6 };;
                this.pianetainCaduta = new int[]{ 4 };;
                break;
            case 4: // leone
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(1);
                this.elemento = Constants.segnoZodiacaleElemento.get(0);
                this.caratteristica = "Vitalità";

                this.pianetaSignoreDelSegno = new int[]{ 0 };;
                this.pianetainEsaltazione = new int[]{ 8 };;
                this.pianetainEsilio = new int[]{ 6, 7 };;
                this.pianetainCaduta = null;;
                break;
            case 5: // vergine
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(2);
                this.elemento = Constants.segnoZodiacaleElemento.get(1);
                this.caratteristica = "Differenziazione";

                this.pianetaSignoreDelSegno = new int[]{ 2 };;
                this.pianetainEsaltazione = new int[]{ 2 };;
                this.pianetainEsilio = new int[]{ 5, 8 };;
                this.pianetainCaduta = new int[]{ 3 };;
                break;
            case 6: // bilancia
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(0);
                this.elemento = Constants.segnoZodiacaleElemento.get(2);
                this.caratteristica = "Sociovolezza";

                this.pianetaSignoreDelSegno = new int[]{ 3 };;
                this.pianetainEsaltazione = new int[]{ 6 };;
                this.pianetainEsilio = new int[]{ 4 };;
                this.pianetainCaduta = new int[]{ 0, 9 };;
                break;
            case 7: // scorpione
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(1);
                this.elemento = Constants.segnoZodiacaleElemento.get(3);
                this.caratteristica = "Trasformazione";

                this.pianetaSignoreDelSegno = new int[]{ 4 };;
                this.pianetainEsaltazione = new int[]{ 7 };;
                this.pianetainEsilio = new int[]{ 3 };;
                this.pianetainCaduta = new int[]{ 1 };;
                break;
            case 8: // sagittario
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(2);
                this.elemento = Constants.segnoZodiacaleElemento.get(0);
                this.caratteristica = "Dualità tra istinti e asprirazioni superiori";

                this.pianetaSignoreDelSegno = new int[]{ 5 };;
                this.pianetainEsaltazione = null;;
                this.pianetainEsilio = new int[]{ 2 };;
                this.pianetainCaduta = null;
                break;
            case 9: // capricorno
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(0);
                this.elemento = Constants.segnoZodiacaleElemento.get(1);
                this.caratteristica = "Elevazione";

                this.pianetaSignoreDelSegno = new int[]{ 6 };;
                this.pianetainEsaltazione = new int[]{ 4 };;
                this.pianetainEsilio = new int[]{ 1, 9 };;
                this.pianetainCaduta = new int[]{ 5 };;
                break;
            case 10: // acquario
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(0);
                this.natura = Constants.segnoZodiacaleNatura.get(1);
                this.elemento = Constants.segnoZodiacaleElemento.get(2);
                this.caratteristica = "Passaggio a stati superiori";

                this.pianetaSignoreDelSegno = new int[]{ 7, 6 };;
                this.pianetainEsaltazione = null;;
                this.pianetainEsilio = new int[]{ 0 };;
                this.pianetainCaduta = new int[]{ 8 };;
                break;
            case 11: // pesci
                this.numeroSegnoZodiacale = numeroSegno;
                this.nomeSegnoZodiacale = Constants.segniZodiacali().get( numeroSegno );
                this.descrizione = appConfig.segniZodiacali().getProperty( String.valueOf(numeroSegno) );

                this.genere = Constants.segnoZodiacaleGenere.get(1);
                this.natura = Constants.segnoZodiacaleNatura.get(2);
                this.elemento = Constants.segnoZodiacaleElemento.get(3);
                this.caratteristica = "Mondo interiore";

                this.pianetaSignoreDelSegno = new int[]{ 5, 8 };;
                this.pianetainEsaltazione = new int[]{ 3 };;
                this.pianetainEsilio = new int[]{ 2 };;
                this.pianetainCaduta = new int[]{ 2 };;
                break;
            default:
                System.out.println("Scelta non valida. Inserisci un numero da 0 a 11.");
                break;
        }
    }

    public int getNumeroSegnoZodiacale() {
        return numeroSegnoZodiacale;
    }

    public String getNomeSegnoZodiacale() {
        return nomeSegnoZodiacale;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getGenere() {
        return genere;
    }

    public String getNatura() {
        return natura;
    }

    public String getElemento() {
        return elemento;
    }

    public String getCaratteristica() {
        return caratteristica;
    }

    public int[] getPianetaSignoreDelSegno() {
        return pianetaSignoreDelSegno;
    }

    public int[] getPianetainEsaltazione() {
        return pianetainEsaltazione;
    }

    public int[] getPianetainEsilio() {
        return pianetainEsilio;
    }

    public int[] getPianetainCaduta() {
        return pianetainCaduta;
    }



}
