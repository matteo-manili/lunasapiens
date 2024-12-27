package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.config.PropertiesConfig;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Properties;

@Component
public class ServizioOmeopatiaAstrologica {

    private static final Logger logger = LoggerFactory.getLogger(ServizioOmeopatiaAstrologica.class);

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    SegnoZodiacale segnoZodiacale;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;


    private int totElementoFuoco = 0; private int totElementoAcqua = 0; private int totElementoTerra = 0; private int totElementoAria = 0;

    private int totElementoFuocoTipoPianeta = 0; private int totElementoAcquaTipoPianeta = 0; private int totElementoTerraTipoPianeta = 0; private int totElementoAriaTipoPianeta = 0;



    private void setNumTotElementi(SegnoZodiacale segno){
        if(segno.getElemento().getCode() == 0){
            totElementoFuoco += 1;

        }else if(segno.getElemento().getCode() == 1){
            totElementoAcqua += 1;

        }else if(segno.getElemento().getCode() == 2){
            totElementoTerra += 1;

        }else if(segno.getElemento().getCode() == 3){
            totElementoAria += 1;
        }
    }

    private void setNumTotElementiPianetiPersonali(SegnoZodiacale segno){
        if( segno.getElemento().getCode() == Constants.Elementi.FUOCO.getCode() ){
            totElementoFuocoTipoPianeta += 1;

        }else if( segno.getElemento().getCode() == Constants.Elementi.ACQUA.getCode() ){
            totElementoAcquaTipoPianeta += 1;

        }else if( segno.getElemento().getCode() == Constants.Elementi.TERRA.getCode() ){
            totElementoTerraTipoPianeta += 1;

        }else if( segno.getElemento().getCode() == Constants.Elementi.ARIA.getCode() ){
            totElementoAriaTipoPianeta += 1;
        }
    }




    public StringBuilder omeopatiaAstrologicaDescrizione_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();

        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate, cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE),
                giornoOraPosizioneDTO, coordinateDTO, propertiesConfig.transitiPianetiSegni_TemaNatale() );

        return omeopatiaAstrologicaDescrizione(result.getPianetiPosizTransitoList(), result.getCasePlacidesList());
    }

    // ############################ OMEOPATIA ASTROLOGIA ########################

    public StringBuilder omeopatiaAstrologicaDescrizione(List<Pianeti> pianetiList, List<CasePlacide> casePlacideArrayList) {

        // Resetta i contatori prima di calcolare
        totElementoFuoco = 0;
        totElementoAcqua = 0;
        totElementoTerra = 0;
        totElementoAria = 0;

        totElementoFuocoTipoPianeta = 0;
        totElementoAcquaTipoPianeta = 0;
        totElementoTerraTipoPianeta = 0;
        totElementoAriaTipoPianeta = 0;

        Properties omeopatiaElementiProperties = propertiesConfig.omeopatiaElementi();
        OmeopatiaAstrologia omeopatiaAstrologia = new OmeopatiaAstrologia();


        SegnoZodiacale ascendente = segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() );
        omeopatiaAstrologia.setAscendente( ascendente );
        setNumTotElementi( ascendente );

        for( Pianeti ite : pianetiList ) {
            SegnoZodiacale segno = segnoZodiacale.getSegnoZodiacale( ite.getNumeroSegnoZodiacale() );

            if( ite.getNumeroPianeta() == Constants.Pianeti.SOLE.getNumero() ) { // pianeta personale
                omeopatiaAstrologia.setPianetaSole( segno );
                setNumTotElementiPianetiPersonali( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.LUNA.getNumero() ) { // pianeta personale
                omeopatiaAstrologia.setPianetaLuna( segno );
                setNumTotElementiPianetiPersonali( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.MERCURIO.getNumero() ) { // pianeta personale
                omeopatiaAstrologia.setPianetaMercurio( segno );
                setNumTotElementiPianetiPersonali( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.VENERE.getNumero() ) { // pianeta personale
                omeopatiaAstrologia.setPianetaVenere( segno );
                setNumTotElementiPianetiPersonali( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.MARTE.getNumero() ) { // pianeta personale
                omeopatiaAstrologia.setPianetaMarte( segno );
                setNumTotElementiPianetiPersonali( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.GIOVE.getNumero() ) {
                omeopatiaAstrologia.setPianetaGiove( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.SATURNO.getNumero() ) {
                omeopatiaAstrologia.setPianetaSaturno( segno );
            }
            else if( ite.getNumeroPianeta() == Constants.Pianeti.URANO.getNumero() ) {
                omeopatiaAstrologia.setPianetaUrano( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.NETTUNO.getNumero() ) {
                omeopatiaAstrologia.setPianetaNettuno( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.PLUTONE.getNumero() ) {
                omeopatiaAstrologia.setPianetaPlutone( segno );
            }

            setNumTotElementi(segno);

        }

        omeopatiaAstrologia.setTotElementoFuoco( totElementoFuoco );
        omeopatiaAstrologia.setTotElementoAcqua( totElementoAcqua );
        omeopatiaAstrologia.setTotElementoTerra( totElementoTerra );
        omeopatiaAstrologia.setTotElementoAria( totElementoAria );

        omeopatiaAstrologia.setTotElementoFuocoTipoPianeta( totElementoFuocoTipoPianeta );
        omeopatiaAstrologia.setTotElementoAcquaTipoPianeta( totElementoAcquaTipoPianeta );
        omeopatiaAstrologia.setTotElementoTerraTipoPianeta( totElementoTerraTipoPianeta );
        omeopatiaAstrologia.setTotElementoAriaTipoPianeta( totElementoAriaTipoPianeta );



        // ############################ FINEEEEEEE ########################

        StringBuilder descTemaNatale = new StringBuilder();


        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Pianeti Segno Elemento</h4><br>");

        descTemaNatale.append("Ascendente in "+ omeopatiaAstrologia.getAscendente().getNomeSegnoZodiacale() +". Elemento: "
                + omeopatiaAstrologia.getAscendente().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(0).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaSole().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(1).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaLuna().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(2).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaMercurio().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(3).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaVenere().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(4).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaMarte().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(5).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaGiove().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(6).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaSaturno().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(7).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaUrano().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(8).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaNettuno().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiList.get(9).descrizionePianetaTipoPianetaSegno() +". Elemento: "
                + omeopatiaAstrologia.getPianetaPlutone().getElemento().getName()+"<br>");


        descTemaNatale.append("<br>");
        descTemaNatale.append("Totale elementi Fuoco: "+omeopatiaAstrologia.getTotElementoFuoco() +"<br>");
        descTemaNatale.append("Totale elementi Acqua: "+omeopatiaAstrologia.getTotElementoAcqua() +"<br>");
        descTemaNatale.append("Totale elementi Terra: "+omeopatiaAstrologia.getTotElementoTerra() +"<br>");
        descTemaNatale.append("Totale elementi Aria: "+omeopatiaAstrologia.getTotElementoAria() +"<br>");


        descTemaNatale.append("<br>");
        descTemaNatale.append("Totale Pianeti Personali elemento Fuoco: "+omeopatiaAstrologia.getTotElementoFuocoTipoPianeta() +"<br>");
        descTemaNatale.append("Totale Pianeti Personali elemento Acqua: "+omeopatiaAstrologia.getTotElementoAcquaTipoPianeta() +"<br>");
        descTemaNatale.append("Totale Pianeti Personali elemento Terra: "+omeopatiaAstrologia.getTotElementoTerraTipoPianeta() +"<br>");
        descTemaNatale.append("Totale Pianeti Personali elemento Aria: "+omeopatiaAstrologia.getTotElementoAriaTipoPianeta() );


        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Descrizione delle 4 Costituzioni</h4><br>");
        descTemaNatale.append( "<p><b>FUOCO: </b>"+ Utils.convertPlainTextToHtml(omeopatiaElementiProperties.getProperty("0")) +"</p>");
        descTemaNatale.append( "<p><b>ACQUA: </b>"+Utils.convertPlainTextToHtml(omeopatiaElementiProperties.getProperty("1")) +"</p>");
        descTemaNatale.append( "<p><b>TERRA: </b>"+Utils.convertPlainTextToHtml(omeopatiaElementiProperties.getProperty("2")) +"</p>");
        descTemaNatale.append( "<p><b>ARIA: </b>"+Utils.convertPlainTextToHtml(omeopatiaElementiProperties.getProperty("3")) +"</p>");


        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Significati ed esempi</h4><br>");
        descTemaNatale.append( "<p>"+Utils.convertPlainTextToHtml(omeopatiaElementiProperties.getProperty("significato")) +"</p>");


        return descTemaNatale;
    }






}

