package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.Util;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

/**
 *         String url = "https://horoscopes.astro-seek.com/calculate-birth-chart-horoscope-online/?input_natal=1" +
 *                 "&send_calculation=1" +
 *                 "&narozeni_den=23" +
 *                 "&narozeni_mesic=1" +
 *                 "&narozeni_rok=1981" +
 *                 "&narozeni_hodina=16" +
 *                 "&narozeni_minuta=16" +
 *                 "&narozeni_sekunda=00" +
 *                 "&narozeni_city=Rome%2C+Italy" +
 *                 "&narozeni_mesto_hidden=Rome" +
 *                 "&narozeni_stat_hidden=IT" +
 *                 "&narozeni_podstat_kratky_hidden=" +
 *                 "&narozeni_sirka_stupne=41" +
 *                 "&narozeni_sirka_minuty=54" +
 *                 "&narozeni_sirka_smer=0" +
 *                 "&narozeni_delka_stupne=12" +
 *                 "&narozeni_delka_minuty=31" +
 *                 "&narozeni_delka_smer=0" +
 *                 "&narozeni_timezone_form=auto" +
 *                 "&narozeni_timezone_dst_form=auto" +
 *                 "&house_system=placidus" +
 *                 "&hid_fortune=1" +
 *                 "&hid_fortune_check=on" +
 *                 "&hid_vertex=1" +
 *                 "&hid_vertex_check=on" +
 *                 "&hid_chiron=1&hid_chiron_check=on" +
 *                 "&hid_lilith=1&hid_lilith_check=on" +
 *                 "&hid_uzel=1&hid_uzel_check=on" +
 *                 "&tolerance=1&aya=" +
 *                 "&tolerance_paral=1.2#tabs_redraw";
 *
 Questi parametri fanno parte di una URL utilizzata per calcolare un tema natale o un oroscopo online sul sito "Astro-Seek". Ogni parametro passato nella URL rappresenta un'informazione necessaria per calcolare il tema natale. Di seguito viene fornita una spiegazione di ciascun parametro:

 input_natal=1: Indica che si sta fornendo una data di nascita per il calcolo del tema natale.
 send_calculation=1: Probabilmente indica che il calcolo deve essere eseguito.
 narozeni_den=23: Giorno di nascita (23).
 narozeni_mesic=1: Mese di nascita (gennaio).
 narozeni_rok=1981: Anno di nascita (1981).
 narozeni_hodina=16: Ora di nascita (16:00, ovvero 4 PM).
 narozeni_minuta=16: Minuto di nascita (16).
 narozeni_sekunda=00: Secondo di nascita (00).
 narozeni_city=Rome%2C+Italy: Città di nascita, codificata in URL, ovvero Roma, Italia.
 narozeni_mesto_hidden=Rome: Città di nascita (Roma), non visibile.
 narozeni_stat_hidden=IT: Stato di nascita (IT per Italia), non visibile.
 narozeni_podstat_kratky_hidden=: Sembra essere vuoto, potrebbe essere un parametro opzionale o non necessario.
 narozeni_sirka_stupne=41: Latitudine della città di nascita in gradi (41 gradi nord).
 narozeni_sirka_minuty=54: Latitudine della città di nascita in minuti (54 minuti nord).
 narozeni_sirka_smer=0: Direzione della latitudine (0 indica nord).
 narozeni_delka_stupne=12: Longitudine della città di nascita in gradi (12 gradi est).
 narozeni_delka_minuty=31: Longitudine della città di nascita in minuti (31 minuti est).
 narozeni_delka_smer=0: Direzione della longitudine (0 indica est).
 narozeni_timezone_form=auto: Fuso orario da utilizzare, impostato su automatico.
 narozeni_timezone_dst_form=auto: Ora legale da utilizzare, impostata su automatico.
 house_system=placidus: Sistema delle case astrologiche da utilizzare (Placidus).
 hid_fortune=1: Indica di includere il Punto della Fortuna nel calcolo.
 hid_fortune_check=on: Indica che il Punto della Fortuna è selezionato.
 hid_vertex=1: Indica di includere il Vertice nel calcolo.
 hid_vertex_check=on: Indica che il Vertice è selezionato.
 hid_chiron=1: Indica di includere Chirone nel calcolo.
 hid_chiron_check=on: Indica che Chirone è selezionato.
 hid_lilith=1: Indica di includere Lilith nel calcolo.
 hid_lilith_check=on: Indica che Lilith è selezionata.
 hid_uzel=1: Indica di includere il Nodo Lunare (Nodo Nord) nel calcolo.
 hid_uzel_check=on: Indica che il Nodo Lunare è selezionato.
 tolerance=1: Tolleranza utilizzata per gli aspetti (probabilmente in gradi).
 aya=: Sembra essere vuoto, potrebbe essere un parametro opzionale o non necessario.
 tolerance_paral=1.2: Tolleranza per i paralleli, impostata a 1.2 gradi.
 #tabs_redraw: Indica che la sezione dei tab deve essere ridisegnata dopo il calcolo.
 */
public class BuildInfoAstrologiaAstroSeek {

    private static final Logger logger = LoggerFactory.getLogger(BuildInfoAstrologiaAstroSeek.class);

    private ArrayList<PianetaPosizTransito> pianetaPosizTransitoArrayList;
    private ArrayList<CasePlacide> casePlacidesArrayList;


    public BuildInfoAstrologiaAstroSeek() {


    }


    public BuildInfoAstrologiaAstroSeek catturaTemaNataleAstroSeek(RestTemplate restTemplate, Cache cache, GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO, Properties transitiPianetiSegniProperties ){

        String urlAstroSeek = "https://horoscopes.astro-seek.com/calculate-birth-chart-horoscope-online/?input_natal=1" +
                "&send_calculation=1" +
                "&narozeni_den=" +giornoOraPosizioneDTO.getGiorno() +
                "&narozeni_mesic=" +giornoOraPosizioneDTO.getMese() +
                "&narozeni_rok=" +giornoOraPosizioneDTO.getAnno() +
                "&narozeni_hodina=" +giornoOraPosizioneDTO.getOra() +
                "&narozeni_minuta=" +giornoOraPosizioneDTO.getMinuti() +
                "&narozeni_sekunda=00" +
                "&narozeni_city=" +coordinateDTO.getCityName()+"%2C+"+coordinateDTO.getStatoName() +
                "&narozeni_mesto_hidden=" + coordinateDTO.getCityName() +
                "&narozeni_stat_hidden=" +coordinateDTO.getStatoCode() +
                "&narozeni_podstat_kratky_hidden=" +
                "&narozeni_sirka_stupne=" +convertLongitudeToHoursMinutes(giornoOraPosizioneDTO.getLat()).getKey() +
                "&narozeni_sirka_minuty="+convertLongitudeToHoursMinutes(giornoOraPosizioneDTO.getLat()).getValue() +
                "&narozeni_sirka_smer=0" +
                "&narozeni_delka_stupne="+convertLongitudeToHoursMinutes(giornoOraPosizioneDTO.getLon()).getKey() +
                "&narozeni_delka_minuty="+convertLongitudeToHoursMinutes(giornoOraPosizioneDTO.getLon()).getValue() +
                "&narozeni_delka_smer=0" +
                "&narozeni_timezone_form=auto" +
                "&narozeni_timezone_dst_form=auto" +
                "&house_system=placidus" +
                "&hid_fortune=1" +
                "&hid_fortune_check=on" +
                "&hid_vertex=1" +
                "&hid_vertex_check=on" +
                "&hid_chiron=1&hid_chiron_check=on" +
                "&hid_lilith=1&hid_lilith_check=on" +
                "&hid_uzel=1&hid_uzel_check=on" +
                "&tolerance=1&aya=" +
                "&tolerance_paral=1.2#tabs_redraw";


        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = cache.get(urlAstroSeek, BuildInfoAstrologiaAstroSeek.class);
        if ( buildInfoAstrologiaAstroSeek != null ){
            return buildInfoAstrologiaAstroSeek;

        }else{

            logger.info( urlAstroSeek );
            ArrayList<PianetaPosizTransito> pianetaPosizTransitoArrayList = new ArrayList<PianetaPosizTransito>();
            ArrayList<CasePlacide> casePlacidesArrayList = new ArrayList<CasePlacide>();
            String html = restTemplate.getForObject(urlAstroSeek, String.class);

            Document document = Jsoup.parse(html);

            // Estrai il contenuto del div con id "vypocty_id_nativ"
            Element divElement = document.getElementById("vypocty_id_nativ");

            if (divElement != null) {
                //System.out.println("Contenuto del div 'vypocty_id_nativ':");
                //System.out.println(divElement.html());
                // ############################ PIANETI POSIZIONE ########################
                for (Element planetElement : divElement.select("div[style^=float: left; width: 80px; margin-left: -5px;]")) {
                    String planetName = planetElement.select("a.tenky-modry").text();
                    String signName = planetElement.nextElementSibling().select("img.astro_symbol").attr("alt");
                    String position = planetElement.nextElementSibling().nextElementSibling().text();

                    Element retrogradeElement = planetElement.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling();
                    boolean isRetrograde = retrogradeElement.text().trim().equals("R");
                    double positionInDegrees = convertToDecimalDegrees(position);

                    System.out.println("Pianeta: " + planetName);
                    System.out.println("Segno: " + signName);
                    System.out.println("Posizione: " + position + " (" + positionInDegrees + " gradi decimali)");
                    System.out.println("Retrogrado: " + (isRetrograde ? "Sì" : "No"));
                    System.out.println();


                    Constants.Pianeti pianeta = Constants.Pianeti.fromNomeEn( planetName );
                    if (pianeta == null){
                        continue;
                    }
                    Constants.SegniZodiacali segno = Constants.SegniZodiacali.fromNomeEn( signName );
                    double gradiTotali = segno.getGradi() + positionInDegrees;
                    String significatoTransitoPianetaSegno = Util.significatoTransitoPianetaSegno(transitiPianetiSegniProperties, pianeta.getNumero(), segno.getNumero());
                    PianetaPosizTransito pianetaPosizTransito = new PianetaPosizTransito(pianeta.getNumero(), pianeta.getNome(), gradiTotali, 0, 0,
                            segno.getNumero(), segno.getNome(), isRetrograde, significatoTransitoPianetaSegno);
                    pianetaPosizTransitoArrayList.add(pianetaPosizTransito);
                }


                // ############################ CASE PLACIDE ########################
                for (Element houseElement : divElement.select("div[style^=float: left; width: 23px; font-size: 1.1em], div[style^=float: left; width: 20px; font-size: 1.1em]")) {
                    String houseName = houseElement.text().replace(":", "");
                    Element siblingElement = houseElement.nextElementSibling();
                    String signName = siblingElement.select("img.astro_symbol").attr("alt");
                    String position = siblingElement.nextElementSibling().text();

                    double positionInDegrees = convertToDecimalDegrees(position);

                    System.out.println("Casa: " + houseName);
                    System.out.println("Segno: " + signName);
                    System.out.println("Posizione: " + position + " (" + positionInDegrees + " gradi decimali)");
                    System.out.println();


                    Constants.Case casa = Constants.Case.fromName( houseName );
                    Constants.SegniZodiacali segno = Constants.SegniZodiacali.fromNomeEn( signName );
                    double gradiTotali = segno.getGradi() + positionInDegrees;

                    CasePlacide casaPlacida = new CasePlacide( casa.getNumero(), casa.getName(), gradiTotali, 0, 0, segno.getNumero(), segno.getNome());
                    casePlacidesArrayList.add( casaPlacida );
                }


                // Ordinamento della lista in base all'attributo nomeCasa
                Collections.sort(casePlacidesArrayList, new Comparator<CasePlacide>() {
                    @Override
                    public int compare(CasePlacide c1, CasePlacide c2) {
                        return Double.compare(c1.getNumeroCasa(), c2.getNumeroCasa());
                    }
                });


                // metto in cache la url e la classe
                buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();
                buildInfoAstrologiaAstroSeek.setPianetaPosizTransitoArrayList( pianetaPosizTransitoArrayList );
                buildInfoAstrologiaAstroSeek.setCasePlacidesArrayList( casePlacidesArrayList );
                cache.put( urlAstroSeek, buildInfoAstrologiaAstroSeek );

                return buildInfoAstrologiaAstroSeek;

            } else {
                System.out.println("Il div con id 'vypocty_id_nativ' non è stato trovato.");
            }

            return null;
        }
    }

    private double convertToDecimalDegrees(String position) {
        String[] parts = position.split("°|'");
        int degrees = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].replace("’", "").trim());
        return degrees + (minutes / 60.0);
    }


    private static Pair convertLongitudeToHoursMinutes(double longitude) {
        // Calcola il numero totale di minuti
        double totalMinutes = longitude * 4;

        // Calcola le ore
        int hours = (int) totalMinutes / 60;

        // Calcola i minuti
        int minutes = (int) totalMinutes % 60;

        // Stampa il risultato
        System.out.println("Longitude in hours and minutes: " + hours + " hours and " + minutes + " minutes");

        Pair<Integer, Integer> pair = new Pair<>(hours, minutes);

        //System.out.println("Primo valore: " + pair. getKey());
        //System.out.println("Secondo valore: " + pair.getValue());

        return pair;
    }



    /*
    private Pair dividiCoordinata(double coordinata){
        // Ottenere la parte intera
        int parteIntera = (int) coordinata;
        // Ottenere la parte decimale
        double parteDecimale = coordinata - parteIntera;
        // Convertire la parte decimale in intero (moltiplicando per 100)
        int parteDecimaleIntera = (int) Math.round(parteDecimale * 100);

        Pair<Integer, Integer> pair = new Pair<>(parteIntera, parteDecimaleIntera);

        //System.out.println("Primo valore: " + pair. getKey());
        //System.out.println("Secondo valore: " + pair.getValue());

        return pair;
    }

     */


    public ArrayList<PianetaPosizTransito> getPianetaPosizTransitoArrayList() {
        return pianetaPosizTransitoArrayList;
    }

    public void setPianetaPosizTransitoArrayList(ArrayList<PianetaPosizTransito> pianetaPosizTransitoArrayList) {
        this.pianetaPosizTransitoArrayList = pianetaPosizTransitoArrayList;
    }

    public ArrayList<CasePlacide> getCasePlacidesArrayList() {
        return casePlacidesArrayList;
    }

    public void setCasePlacidesArrayList(ArrayList<CasePlacide> casePlacidesArrayList) {
        this.casePlacidesArrayList = casePlacidesArrayList;
    }

}
