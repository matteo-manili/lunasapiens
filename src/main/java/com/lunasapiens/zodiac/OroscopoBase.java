package com.lunasapiens.zodiac;

/**
 * JD (Julian Date): Questo è un valore numerico che rappresenta la data in formato Julian Date, spesso utilizzato in astronomia e astrologia per
 * calcolare le posizioni dei pianeti e altri oggetti celesti.
 *
 * F (Fattore): Questo valore rappresenta un fattore o una posizione specifica in un calcolo astrologico. Potrebbe essere utilizzato in calcoli
 * astrologici più complessi.
 *
 * SZ (Segno Zodiacale): Questo valore rappresenta il segno zodiacale. Nel tuo esempio, "9°12'28"" potrebbe rappresentare una posizione angolare all'interno
 * di un determinato segno zodiacale.
 */
public class OroscopoBase {

    private String julianDate;
    private String fattore;
    private int gradi;
    private int minuti;
    private int secondi;
    private String nomeSegnoZodiacale;


    public OroscopoBase(String julianDate, String fattore, int gradi, int minuti, int secondi, String nomeSegnoZodiacale) {
        this.julianDate = julianDate;
        this.fattore = fattore;
        this.gradi = gradi;
        this.minuti = minuti;
        this.secondi = secondi;
        this.nomeSegnoZodiacale = nomeSegnoZodiacale; // <-- da sempre Ariete, verificare perché
    }

    public String getJulianDate() {
        return julianDate;
    }

    public String getFattore() {
        return fattore;
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
        return "OroscopoBase{" +
                "julianDate='" + julianDate + '\'' +
                ", fattore='" + fattore + '\'' +
                ", gradi=" + gradi +
                ", minuti=" + minuti +
                ", secondi=" + secondi +
                ", Segno Zodiacale='" + nomeSegnoZodiacale + '\'' +
                '}';
    }
}
