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

import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

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

    private final List<PianetaPosizTransito> pianetaPosizTransitoArrayList;
    private final List<CasePlacide> casePlacidesArrayList;


    public BuildInfoAstrologiaAstroSeek() {
        this.pianetaPosizTransitoArrayList = Collections.emptyList();
        this.casePlacidesArrayList = Collections.emptyList();
    }

    public BuildInfoAstrologiaAstroSeek(List<PianetaPosizTransito> pianetaPosizTransitoArrayList, List<CasePlacide> casePlacidesArrayList) {
        this.pianetaPosizTransitoArrayList = Collections.unmodifiableList(pianetaPosizTransitoArrayList);
        this.casePlacidesArrayList = Collections.unmodifiableList(casePlacidesArrayList);
    }

    public static String pianetaDomicioSegnoCasa = "(Pianeta governatore della Casa)";

    public static String temaNataleIstruzioneBOTSystem(String temaNataleDescrizione, LocalDateTime datetimeNascita, String luogoNascita){

        return "- Sei un astrologo informato sul tema natale dell'utente, rispondi alle domande dell'utente riguardo il suo tema natale.\n\n" +

        "- Le Case astrologiche ed i pineti relativi alle Case, indicano il futuro e gli eventi dell'utente.\n" +
        "- I transiti dei Pianeti indicano le caratteristiche personali dell'utente.\n" +
        "- le interpretazioni dei pianeti (anche quelli governatori della casa) nelle Case, vanno declinate in base al significato della Casa, " +
        "al significato del Pianeta, al Significato degli Aspetti del pianeta e al Significato di Pianeta Retrogrado (se è retrogrado).\n"+

        "- Le interpretazioni dei Transiti dei Pianeti, vanno declinati in base al Significato del Pianeta, " +
        "al Significato degli Aspetti del pianeta e al Significato di Pianeta Retrogrado se esso è retrogrado.\n\n" +


        "- Data del tema natale e data nascita dell'utente: "+datetimeNascita.format(Constants.DATE_TIME_FORMATTER) +"\n" +
        "- Anni dell'utente: "+calculateAge(datetimeNascita)+"\n"+
        "- Luogo di nascita dell'utente: "+luogoNascita +"\n\n" +

        "- Non puoi creare un tema natale in nessun modo. In astrologia non conosci gli argomenti di: karma, nodo karmico, aspetto stellium, " +
        "luna piena, nodi lunari nord sud, lilith chirone.\n" +
        "- Non dare risposte che vanno oltre l'argomento del tema natale dell'utente.\n\n" +

        "- Descrizione tema natale dell'utente: \n" + Util.convertHtmlToPlainText(temaNataleDescrizione);
    }


    public static int calculateAge(LocalDateTime dateOfBirth) {
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.between(dateOfBirth.toLocalDate(), now.toLocalDate());
        return period.getYears();
    }


    public BuildInfoAstrologiaAstroSeek catturaTemaNataleAstroSeek(RestTemplate restTemplate, Cache cache, GiornoOraPosizioneDTO giornoOraPosizioneDTO,
                                                                   CoordinateDTO coordinateDTO, Properties transitiPianetiSegniProperties ){

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
                "&narozeni_sirka_stupne=" +convertCoordonataToDegreesMinutes(giornoOraPosizioneDTO.getLat()).getKey() +
                "&narozeni_sirka_minuty="+convertCoordonataToDegreesMinutes(giornoOraPosizioneDTO.getLat()).getValue() +
                "&narozeni_sirka_smer=0" +
                "&narozeni_delka_stupne="+convertCoordonataToDegreesMinutes(giornoOraPosizioneDTO.getLon()).getKey() +
                "&narozeni_delka_minuty="+convertCoordonataToDegreesMinutes(giornoOraPosizioneDTO.getLon()).getValue() +
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
            List<PianetaPosizTransito> pianetaPosizTransitoArrayList = new ArrayList<PianetaPosizTransito>();
            List<CasePlacide> casePlacidesArrayList = new ArrayList<CasePlacide>();
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
                    PianetaPosizTransito pianetaPosizTransito = new PianetaPosizTransito(pianeta.getNumero(), pianeta.getNome(), gradiTotali, (int)dammiGradiEMinuti(position).getKey(),
                            (int)dammiGradiEMinuti(position).getValue(), segno.getNumero(), segno.getNome(), isRetrograde, significatoTransitoPianetaSegno);
                    pianetaPosizTransitoArrayList.add(pianetaPosizTransito);
                }


                // ############################ CASE PLACIDE ########################
                for (Element houseElement : divElement.select("div[style^=float: left; width: 23px; font-size: 1.1em], div[style^=float: left; width: 20px; font-size: 1.1em]")) {
                    String houseName = houseElement.text().replace(":", "");
                    Element siblingElement = houseElement.nextElementSibling();
                    String signName = siblingElement.select("img.astro_symbol").attr("alt");
                    String position = siblingElement.nextElementSibling().text();

                    Constants.Case casa = Constants.Case.fromCode( houseName );
                    Constants.SegniZodiacali segno = Constants.SegniZodiacali.fromNomeEn( signName );
                    double gradiTotali = segno.getGradi() + convertToDecimalDegrees(position);

                    System.out.println("Casa: " + houseName);
                    System.out.println("Segno: " + signName);
                    System.out.println("valore: " + position + " "+" valore in decimali: "+gradiTotali);
                    System.out.println();

                    CasePlacide casaPlacida = new CasePlacide( casa.getNumero(), casa.getName(), gradiTotali, (int)dammiGradiEMinuti(position).getKey(),
                            (int)dammiGradiEMinuti(position).getValue(), segno.getNumero(), segno.getNome());
                    casePlacidesArrayList.add( casaPlacida );
                }


                // Ordinamento della lista in base all'attributo nomeCasa
                Collections.sort(casePlacidesArrayList, new Comparator<CasePlacide>() {
                    @Override
                    public int compare(CasePlacide c1, CasePlacide c2) {
                        return Double.compare(c1.getNumeroCasa(), c2.getNumeroCasa());
                    }
                });


                buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek(pianetaPosizTransitoArrayList, casePlacidesArrayList);
                cache.put(urlAstroSeek, buildInfoAstrologiaAstroSeek);



                return buildInfoAstrologiaAstroSeek;

            } else {
                System.out.println("Il div con id 'vypocty_id_nativ' non è stato trovato.");
            }
            return null;
        }
    }

    private double convertToDecimalDegrees(String position) {
        Pair<Integer, Integer> pair = dammiGradiEMinuti(position);
        return pair.getKey() + (pair.getValue() / 60.0);
    }

    private static Pair dammiGradiEMinuti(String position) {
        String[] parts = position.split("°|'");
        int degrees = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].replace("’", "").trim());
        Pair<Integer, Integer> pair = new Pair<>(degrees, minutes);
        return pair;
    }




    private static Pair convertCoordonataToDegreesMinutes(double coordinata) {
        int degrees = (int) coordinata;
        double fractionalPart = coordinata - degrees;
        // Convertire la parte decimale in minuti
        double minutes = fractionalPart * 60;
        int minutesInt = (int) minutes;
        double seconds = (minutes - minutesInt) * 60;
        Pair<Integer, Integer> pair = new Pair<>(degrees, minutesInt);
        return pair;
    }


    public List<PianetaPosizTransito> getPianetaPosizTransitoArrayList() { return pianetaPosizTransitoArrayList; }

    public List<CasePlacide> getCasePlacidesArrayList() { return casePlacidesArrayList; }



        /*
    public void setPianetaPosizTransitoArrayList(ArrayList<PianetaPosizTransito> pianetaPosizTransitoArrayList) {
        this.pianetaPosizTransitoArrayList = pianetaPosizTransitoArrayList;
    }

        public void setCasePlacidesArrayList(ArrayList<CasePlacide> casePlacidesArrayList) {
        this.casePlacidesArrayList = casePlacidesArrayList;
    }
*/





}
