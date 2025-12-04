package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.RelationshipOption;
import com.lunasapiens.utils.UtilsZodiac;
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

    private final List<Pianeti> pianetiList; private final List<CasePlacide> casePlacidesList;

    public List<Pianeti> getPianetiPosizTransitoList() { return pianetiList; }
    public List<CasePlacide> getCasePlacidesList() { return casePlacidesList; }

    public BuildInfoAstrologiaAstroSeek() {
        this.pianetiList = Collections.emptyList(); this.casePlacidesList = Collections.emptyList();
    }

    public BuildInfoAstrologiaAstroSeek(List<Pianeti> pianetaArrayList, List<CasePlacide> casePlacidesArrayList) {
        this.pianetiList = Collections.unmodifiableList(pianetaArrayList);
        this.casePlacidesList = Collections.unmodifiableList(casePlacidesArrayList);
    }

    public static String pianetaDomicioSegnoCasa = "(Pianeta governatore della Casa)";


    public static StringBuilder sinastriaIstruzioneBOTSystem(String relationshipString, String nome, String nome_2,
         String temaNataleDescrizione, String temaNataleDescrizione_2, String significatiTemaNataleDescrizione,
         LocalDateTime datetimeNascita, LocalDateTime datetimeNascita_2,
         String luogoNascita, String luogoNascita_2){

        StringBuilder textSystemBuilder = new StringBuilder();
        textSystemBuilder.append("SEI UN ASTROLOGO INFORMATO SULLA SINASTRIA DI: "+nome+" E "+nome_2+", l'UTENTE CHE TI FA LE DOMANDE è "+nome+" " +
                "LA LORO RELAZINE è DI TIPO "+ findRelationshipOptionByCode(relationshipString).getDescription()+". "+
                "RISPONDI ALLE DOMANDE DELL'UTENTE RIGUARDO LA SINASTRIA. " +
                "NON AGGIUNGERE E NON INVENTARE NIENTE OLTRE LE INFORMAZIONI FORNITE.\n\n")

                .append("- LE CASE ED I PIANETI NELLE CASE, INDICANO IL FUTURO E LE INCLINAZIONI CHE AVRÀ L'UTENTE.\n")
                .append("- le interpretazioni delle Case vanno declinate in base a: significato della Casa, ai Pianeti nella casa, " +
                        "al significato del Segno della Casa e al significato di Pianeta Retrogrado se il Pianeta è retrogrado.\n\n")

                .append("- I TRANSITI DEI PIANETI INDICANO LE CARATTERISTICHE DELLA PERSONALITÀ DELL'UTENTE.\n")
                .append("- I Transiti dei Pianeti vanno declinati in base a: significato del Pianeta, significato dell'Aspetto " +
                        "del Pianeta e al Significato di Pianeta Retrogrado se il Pianeta è retrogrado." +
                        "I Transiti dei Pianeti non sono attuali ma sono della Data del giorno del Tema Natale.\n\n")

                .append("- Non puoi creare un Tema Natale. Non puoi sapere e interpretare i transiti attuali o di un'altra data. " +
                        "In astrologia non conosci gli argomenti di: karma, stellium, luna piena, nodi lunari nord sud, rivoluzione solare, lilith, " +
                        "chirone.")

                .append("\n\n")


// TODO DA AGGIUNGERE
/*
La sinastria è una tecnica astrologica che confronta le carte natali di due persone per determinare la compatibilità e le dinamiche della loro relazione. Ecco le principali caratteristiche da tenere in considerazione per fare una sinastria tra le carte natali di una coppia:

1. Posizioni dei Sole
Segni Solari: La posizione del Sole nei due temi natali rappresenta la personalità di base e l'essenza del sé. I segni solari possono indicare affinità o differenze fondamentali nel modo di esprimere la propria energia vitale.
Aspetti del Sole: Gli aspetti che i due Soli formano tra di loro (congiunzione, quadratura, trigono, opposizione, ecc.) forniscono indizi su quanto i partner siano in sintonia a livello di identità e volontà.
2. Posizioni delle Lune
Segni Lunari: La Luna rappresenta l'emotività, i bisogni più intimi e il modo di nutrire e ricevere supporto emotivo. La compatibilità delle Lune può indicare quanto i partner siano in sintonia sul piano emotivo.
Aspetti della Luna: Gli aspetti tra le Lune e altri pianeti sono cruciali per valutare la compatibilità emotiva e il modo in cui i partner si comprendono a livello sentimentale.
3. Ascendenti
Segni Ascendenti: L'Ascendente rappresenta l'approccio alla vita, la personalità esteriore e il modo in cui ci si presenta al mondo. Gli Ascendenti in segni compatibili possono indicare una sintonia nella visione del mondo e nell'approccio agli eventi della vita.
Relazione tra gli Ascendenti: Le interazioni tra gli Ascendenti (tramite aspetti o la compatibilità dei segni) possono indicare come i partner si percepiscono e si attraggono a livello esteriore.
 */

                //.append("----------------------------------------------\n")
                //.append("- Posizioni dei soli di "+nome+" e "+nome_2)

                //.append("- Posizioni delle Lune di "+nome+" e "+nome_2)

                //.append("- Ascendenti "+nome+" e "+nome_2)

                .append("----------------------------------------------\n")

                .append("- TEMA NATALE DELL'UTENTE "+nome+":\n\n")
                .append("Data del Tema Natale e data nascita dell'Utente: "+datetimeNascita.format(Constants.DATE_TIME_FORMATTER) +"\n")
                .append("Anni dell'Utente: "+calculateAge(datetimeNascita)+"\n")
                .append("Luogo di nascita dell'Utente: "+luogoNascita +"\n")
                .append( Utils.convertHtmlToPlainText(temaNataleDescrizione) )

                .append("\n\n")
                .append("----------------------------------------------\n")

                .append("- TEMA NATALE DELL'UTENTE "+nome_2+":\n\n")
                .append("Data del Tema Natale e data nascita dell'Utente: "+datetimeNascita_2.format(Constants.DATE_TIME_FORMATTER) +"\n")
                .append("Anni dell'Utente: "+calculateAge(datetimeNascita_2)+"\n")
                .append("Luogo di nascita dell'Utente: "+luogoNascita_2 +"\n")
                .append( Utils.convertHtmlToPlainText(temaNataleDescrizione_2) )

                .append("\n\n")
                .append("----------------------------------------------\n")

                .append("- Significati:\n")
                .append( Utils.convertHtmlToPlainText(significatiTemaNataleDescrizione) );

        return textSystemBuilder;
    }



    public static RelationshipOption findRelationshipOptionByCode(String code) {
        // Cicliamo attraverso la lista RELATIONSHIP_OPTIONS per trovare il codice corrispondente
        return Constants.RELATIONSHIP_OPTIONS.stream()
                .filter(option -> option.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null); // Puoi restituire null o gestire l'assenza del codice come preferisci
    }


    @Deprecated
    public static StringBuilder temaNataleIstruzioneBOTSystem_OLD(String temaNataleDescrizione, LocalDateTime datetimeNascita, String luogoNascita){
        StringBuilder textSystemBuilder = new StringBuilder();
        textSystemBuilder.append("SEI UN ASTROLOGO INFORMATO SUL TEMA NATALE DELL'UTENTE, " +
                "RISPONDI ALLE DOMANDE DELL'UTENTE RIGUARDO IL TEMA NATALE SOTTO DESCRITTO. NON AGGIUNGERE E NON INVENTARE NIENTE " +
                "OLTRE LE INFORMAZIONI FORNITE.\n\n")

                .append("- LE CASE ED I PIANETI NELLE CASE, INDICANO IL FUTURO E LE INCLINAZIONI CHE AVRÀ L'UTENTE.\n")
                .append("- le interpretazioni delle Case vanno declinate in base a: significato della Casa, ai Pianeti nella casa, " +
                        "al significato del Segno della Casa e al significato di Pianeta Retrogrado se il Pianeta è retrogrado.\n\n")

                .append("- I TRANSITI DEI PIANETI INDICANO LE CARATTERISTICHE DELLA PERSONALITÀ DELL'UTENTE.\n")
                .append("- I Transiti dei Pianeti vanno declinati in base a: significato del Pianeta, significato dell'Aspetto " +
                        "del Pianeta e al Significato di Pianeta Retrogrado se il Pianeta è retrogrado." +
                        "I Transiti dei Pianeti non sono attuali ma sono della Data del giorno del Tema Natale.\n\n")

                .append("- Non puoi creare un Tema Natale. Non puoi sapere e interpretare i transiti attuali o di un'altra data. " +
                        "In astrologia non conosci gli argomenti di: karma, stellium, luna piena, nodi lunari nord sud, rivoluzione solare, lilith, " +
                        "chirone.\n\n")

                .append("- Data del Tema Natale e data nascita dell'Utente: "+datetimeNascita.format(Constants.DATE_TIME_FORMATTER) +"\n")
                .append("- Anni dell'Utente: "+calculateAge(datetimeNascita)+"\n")
                .append("- Luogo di nascita dell'Utente: "+luogoNascita +"\n\n\n")

                .append("- Tema natale dell'Utente:"+"\n")
                .append( Utils.convertHtmlToPlainText(temaNataleDescrizione) );
        return textSystemBuilder;
    }

    public static StringBuilder temaNataleIstruzioneBOTSystem(String temaNataleDescrizione, LocalDateTime datetimeNascita, String luogoNascita){
        StringBuilder textSystemBuilder = new StringBuilder();
        textSystemBuilder.append("SEI UN ASTROLOGO INFORMATO SUL TEMA NATALE DELL'UTENTE, " +
                "RISPONDI ALLE DOMANDE DELL'UTENTE RIGUARDO IL TEMA NATALE SOTTO DESCRITTO. " +
                "NON AGGIUNGERE E NON INVENTARE NIENTE OLTRE LE INFORMAZIONI FORNITE.\n\n")

                .append("- LE CASE ED I PIANETI NELLE CASE, INDICANO LE INCLINAZIONI.\n")
                .append("Le interpretazioni delle Case vanno fatte in base a: il significato del Segno della Casa, " +
                        "il significato dei Pianeti nella casa e al significato di Pianeta Retrogrado se il Pianeta è retrogrado.\n\n")

                .append("- GLI ASPETTI INDICANO LE INFLUENZE SULLA PERSONALITA', LE DINAMICHE INTERIORI E LE ESPERIENZE DI VITA.\n\n")

                .append("- I TRANSITI DEI PIANETI INDICANO LE CARATTERISTICHE DELLA PERSONALITÀ.\n\n")

                .append("- Non puoi creare un Tema Natale. Non puoi sapere e interpretare i transiti attuali o di un'altra data. " +
                        "In astrologia non conosci gli argomenti di: stellium, luna piena, rivoluzione solare.\n\n")

                .append("- Data del Tema Natale e data nascita dell'Utente: "+datetimeNascita.format(Constants.DATE_TIME_FORMATTER) +"\n")
                .append("- Anni dell'Utente: "+calculateAge(datetimeNascita)+"\n")
                .append("- Luogo di nascita dell'Utente: "+luogoNascita +"\n\n")

                .append("- TEMA NATALE:"+"\n\n")
                .append( Utils.convertHtmlToPlainText(temaNataleDescrizione) );
        return textSystemBuilder;
    }


    public static int calculateAge(LocalDateTime dateOfBirth) {
        LocalDateTime now = Utils.getNowRomeEurope().toLocalDateTime();
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
                "&narozeni_sirka_stupne=" + UtilsZodiac.convertCoordinataToDegreesMinutes(giornoOraPosizioneDTO.getLat()).getKey() +
                "&narozeni_sirka_minuty="+ UtilsZodiac.convertCoordinataToDegreesMinutes(giornoOraPosizioneDTO.getLat()).getValue() +
                "&narozeni_sirka_smer=0" +
                "&narozeni_delka_stupne="+ UtilsZodiac.convertCoordinataToDegreesMinutes(giornoOraPosizioneDTO.getLon()).getKey() +
                "&narozeni_delka_minuty="+ UtilsZodiac.convertCoordinataToDegreesMinutes(giornoOraPosizioneDTO.getLon()).getValue() +
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
            List<Pianeti> pianetiList = new ArrayList<Pianeti>();
            List<CasePlacide> casePlacidesList = new ArrayList<CasePlacide>();
            String html = restTemplate.getForObject(urlAstroSeek, String.class);

            Document document = Jsoup.parse(html);

            // Estrai il contenuto del div con id "vypocty_id_nativ"
            Element divElement = document.getElementById("vypocty_id_nativ");

            if (divElement != null) {
                //System.out.println("Contenuto del div 'vypocty_id_nativ':");
                //System.out.println(divElement.html());
                // ############################ PIANETI POSIZIONE ########################
                System.out.println("----- PIANETI -----");
                for (Element planetElement : divElement.select("div[style^=float: left; width: 80px; margin-left: -5px;]")) {
                    String planetName = planetElement.select("a.tenky-modry").text();
                    String signName = planetElement.nextElementSibling().select("img.astro_symbol").attr("alt");
                    String positionGradiString = planetElement.nextElementSibling().nextElementSibling().text();
                    Element retrogradeElement = planetElement.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling();

                    if ( planetName.trim().isEmpty() ){
                        continue;
                    }

                    boolean isRetrograde = retrogradeElement.text().trim().equals("R");
                    double positionInDecimal = UtilsZodiac.convertDegreesToDecimal(positionGradiString);
                    Constants.SegniZodiacali segnoZodiacale = Constants.SegniZodiacali.fromNomeEn( signName );
                    double positionInDecimalTotal = segnoZodiacale.getGradi() + positionInDecimal;

                    //System.out.println("Pianeta: " +planetName+ " Segno: " +signName);
                    //System.out.println("Posizione: "+positionGradiString+ " | decimali "+positionInDecimal+ " | positionInDecimalTotal "+positionInDecimalTotal);
                    //System.out.println("Retrogrado: " + (isRetrograde ? "Sì" : "No"));
                    //System.out.println();

                    Constants.Pianeti pianetaConstant = Constants.Pianeti.fromNomeAstroSeek( planetName );
                    Pair gradiMinutiPair = UtilsZodiac.dammiGradiEMinutiPair(positionGradiString);
                    String significatoTransitoPianetaSegno = UtilsZodiac.significatoTransitoPianetaSegno(transitiPianetiSegniProperties, pianetaConstant.getNumero(), segnoZodiacale.getNumero());
                    Pianeti pianeta = new Pianeti(pianetaConstant.getNumero(), pianetaConstant.getNome(), positionInDecimalTotal, (int)gradiMinutiPair.getKey(),
                            (int)gradiMinutiPair.getValue(), segnoZodiacale.getNumero(), segnoZodiacale.getNome(), isRetrograde, significatoTransitoPianetaSegno);
                    pianetiList.add( pianeta );


                    // aggiungo pianeta Nodo Sud (sarebbe opposto al pianeta Nodo M, o anche chiamato Nodo N. Sono simili ma non proprio gli stessi)
                    if( pianeta.getNumeroPianeta() == Constants.Pianeti.NODE_M.getNumero()){
                        Pair gradiMinutiNodoOppostoPair = UtilsZodiac.calcolaPosizionePianetaOppostoInGradiEMinuti((int)gradiMinutiPair.getKey(), (int)gradiMinutiPair.getValue());
                        double positionInDecimalTotalNodoSud = UtilsZodiac.calcolaPosizionePianetaOpposto(positionInDecimalTotal);
                        Constants.SegniZodiacali segnoOpposto = UtilsZodiac.calcolaSegnoOpposto(segnoZodiacale);
                        Pianeti pianetaNodeS = new Pianeti(Constants.Pianeti.NODE_S.getNumero(), Constants.Pianeti.NODE_S.getNome(), positionInDecimalTotalNodoSud, (int)gradiMinutiNodoOppostoPair.getKey(),
                                (int)gradiMinutiNodoOppostoPair.getValue(), segnoOpposto.getNumero(), segnoOpposto.getNome(), isRetrograde, significatoTransitoPianetaSegno);
                        pianetiList.add( pianetaNodeS );
                    }
                }


                // ############################ CASE PLACIDE ########################
                System.out.println("----- CASE PLACIDE -----");
                for (Element houseElement : divElement.select("div[style^=float: left; width: 23px; font-size: 1.1em], div[style^=float: left; width: 20px; font-size: 1.1em]")) {
                    String houseName = houseElement.text().replace(":", "");
                    Element siblingElement = houseElement.nextElementSibling();
                    String signName = siblingElement.select("img.astro_symbol").attr("alt");
                    String positionGradiString = siblingElement.nextElementSibling().text();

                    double positionInDecimal = UtilsZodiac.convertDegreesToDecimal(positionGradiString);
                    Constants.SegniZodiacali SegnoZodiacale = Constants.SegniZodiacali.fromNomeEn( signName );
                    double positionInDecimalTotal = SegnoZodiacale.getGradi() + positionInDecimal;

                    //System.out.println("Casa: " +houseName+ " Segno: " +signName);
                    //System.out.println("posizione: "+positionGradiString+ " | positionInDecimal "+positionInDecimal+ " | positionInDecimalTotal "+positionInDecimalTotal);
                    //System.out.println();

                    Constants.Case casa = Constants.Case.fromCode( houseName );
                    Pair gradiMinutiPair = UtilsZodiac.dammiGradiEMinutiPair(positionGradiString);
                    CasePlacide casaPlacida = new CasePlacide( casa.getNumero(), casa.getName(), positionInDecimalTotal, (int)gradiMinutiPair.getKey(),
                            (int)gradiMinutiPair.getValue(), SegnoZodiacale.getNumero(), SegnoZodiacale.getNome());
                    casePlacidesList.add( casaPlacida );
                }

                // Ordinamento della lista in base all'attributo nomeCasa
                Collections.sort(casePlacidesList, new Comparator<CasePlacide>() {
                    @Override
                    public int compare(CasePlacide c1, CasePlacide c2) {
                        return Double.compare(c1.getNumeroCasa(), c2.getNumeroCasa());
                    }
                });


                buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek(pianetiList, casePlacidesList);
                cache.put(urlAstroSeek, buildInfoAstrologiaAstroSeek);

                return buildInfoAstrologiaAstroSeek;

            } else {
                System.out.println("Il div con id 'vypocty_id_nativ' non è stato trovato.");
            }
            return null;
        }
    }


    public static boolean contieneCaratteriAnomali(String str) {
        // Controlla se la stringa contiene caratteri di controllo, invisibili o non stampabili
        return str.matches(".*[\\p{C}\\P{Print}].*");
    }




}
