package com.lunasapiens.zodiac;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Properties;

@Component
public class SegnoZodiacale {

    private int numeroSegnoZodiacale;
    private String nomeSegnoZodiacale;
    private String descrizione;
    private String descrizioneMin;

    private String genere;
    private String natura;
    private String elemento;
    private String caratteristica;

    private int[] pianetiSignoreDelSegno;
    private int[] pianetainEsaltazione;
    private int[] pianetainEsilio;
    private int[] pianetainCaduta;


    @Autowired
    private AppConfig appConfig;


    public SegnoZodiacale getSegnoZodiacale(int numeroSegno) {
        SegnoZodiacale segnoZodiacale = new SegnoZodiacale();
        Properties segniZodiacali = appConfig.segniZodiacali();

        switch (numeroSegno) {
            case 0: // ariete
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(0);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(0);
                segnoZodiacale.caratteristica = "Impulsività";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 4 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 0 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 3 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 6 };;
                break;
            case 1: // toro
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(1);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(1);
                segnoZodiacale.caratteristica = "Sforzo";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 3 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 1 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 4 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 7 };;
                break;
            case 2: // gemelli
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(2);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(2);
                segnoZodiacale.caratteristica = "Dualità";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 2 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 9 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 5 };;
                segnoZodiacale.pianetainCaduta = null;;
                break;
            case 3: // cancro
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(0);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(3);
                segnoZodiacale.caratteristica = "Passività";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 1 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 5 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 6 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 4 };;
                break;
            case 4: // leone
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(1);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(0);
                segnoZodiacale.caratteristica = "Vitalità";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 0 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 8 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 6, 7 };;
                segnoZodiacale.pianetainCaduta = null;;
                break;
            case 5: // vergine
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(2);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(1);
                segnoZodiacale.caratteristica = "Differenziazione";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 2 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 2 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 5, 8 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 3 };;
                break;
            case 6: // bilancia
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(0);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(2);
                segnoZodiacale.caratteristica = "Sociovolezza";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 3 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 6 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 4 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 0, 9 };;
                break;
            case 7: // scorpione
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(1);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(3);
                segnoZodiacale.caratteristica = "Trasformazione";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 9, 4 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 7 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 3 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 1 };;
                break;
            case 8: // sagittario
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(2);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(0);
                segnoZodiacale.caratteristica = "Dualità tra istinti e asprirazioni superiori";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 5 };;
                segnoZodiacale.pianetainEsaltazione = null;;
                segnoZodiacale.pianetainEsilio = new int[]{ 2 };;
                segnoZodiacale.pianetainCaduta = null;
                break;
            case 9: // capricorno
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(0);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(1);
                segnoZodiacale.caratteristica = "Elevazione";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 6 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 4 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 1, 9 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 5 };;
                break;
            case 10: // acquario
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(0);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(1);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(2);
                segnoZodiacale.caratteristica = "Passaggio a stati superiori";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 7, 6 };;
                segnoZodiacale.pianetainEsaltazione = null;;
                segnoZodiacale.pianetainEsilio = new int[]{ 0 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 8 };;
                break;
            case 11: // pesci
                segnoZodiacale.numeroSegnoZodiacale = numeroSegno;
                segnoZodiacale.nomeSegnoZodiacale = Constants.SegniZodiacali.fromNumero(numeroSegno).getNome();
                segnoZodiacale.descrizione = segniZodiacali.getProperty(String.valueOf(numeroSegno));;
                segnoZodiacale.descrizioneMin = segniZodiacali.getProperty(String.valueOf(numeroSegno)+"_min");;

                segnoZodiacale.genere = Constants.segnoZodiacaleGenere.get(1);
                segnoZodiacale.natura = Constants.segnoZodiacaleNatura.get(2);
                segnoZodiacale.elemento = Constants.segnoZodiacaleElemento.get(3);
                segnoZodiacale.caratteristica = "Mondo interiore";

                segnoZodiacale.pianetiSignoreDelSegno = new int[]{ 8, 5 };;
                segnoZodiacale.pianetainEsaltazione = new int[]{ 3 };;
                segnoZodiacale.pianetainEsilio = new int[]{ 2 };;
                segnoZodiacale.pianetainCaduta = new int[]{ 2 };;
                break;
            default:
                System.out.println("Scelta non valida. Inserisci un numero da 0 a 11.");
                break;
        }
        return segnoZodiacale;
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

    public String getDescrizioneMin() {
        return descrizioneMin;
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

    /**
     * Sono quasi sempre solo 1, a parte acquario e pesci che ne hanno 2
     * @return
     */
    public int[] getPianetiSignoreDelSegno() {
        return pianetiSignoreDelSegno;
    }

    /**
     * Attenzione!!! alcuni segni non hanno pianeti e restituisce NULL
     * @return
     */
    public int[] getPianetainEsaltazione() {
        return pianetainEsaltazione;
    }

    /**
     * Attenzione!!! alcuni segni non hanno pianeti e restituisce NULL
     * @return
     */
    public int[] getPianetainEsilio() {
        return pianetainEsilio;
    }

    /**
     * Attenzione!!! alcuni segni non hanno pianeti e restituisce NULL
     * @return
     */
    public int[] getPianetainCaduta() {
        return pianetainCaduta;
    }



}
