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




    public StringBuilder omeopatiaAstrologicaDescrizione_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();

        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate,
                cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE), giornoOraPosizioneDTO, coordinateDTO,
                propertiesConfig.transitiPianetiSegni_TemaNatale() );

        return omeopatiaAstrologicaDescrizione(result.getPianetaPosizTransitoArrayList(), result.getCasePlacidesArrayList());
    }






    public StringBuilder omeopatiaAstrologicaDescrizione(List<PianetaPosizTransito> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {

        Properties omeopatiaElementiProperties = propertiesConfig.omeopatiaElementi();


        // ############################ OMEOPATIA ASTROLOGIA ########################
        OmeopatiaAstrologia omeopatiaAstrologia = new OmeopatiaAstrologia();



        int totElementoFuoco = 0; int totElementoAcqua = 0; int totElementoTerra = 0; int totElementoAria = 0;


        SegnoZodiacale ascendente = segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() );
        omeopatiaAstrologia.setAscendente( ascendente );

        if(ascendente.getElemento().getCode() == 0){
            totElementoFuoco = totElementoFuoco + 1;

        }else if(ascendente.getElemento().getCode() == 1){
            totElementoAcqua = totElementoAcqua + 1;

        }else if(ascendente.getElemento().getCode() == 2){
            totElementoTerra = totElementoTerra + 1;

        }else if(ascendente.getElemento().getCode() == 3){
            totElementoAria = totElementoAria + 1;
        }


        for( PianetaPosizTransito ite :pianetiTransiti ) {
            SegnoZodiacale segno = segnoZodiacale.getSegnoZodiacale( ite.getNumeroSegnoZodiacale() );


            if( ite.getNumeroPianeta() == Constants.Pianeti.SOLE.getNumero() ) {
                omeopatiaAstrologia.setPianetaSole( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.LUNA.getNumero() ) {
                omeopatiaAstrologia.setPianetaLuna( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.MERCURIO.getNumero() ) {
                omeopatiaAstrologia.setPianetaMercurio( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.VENERE.getNumero() ) {
                omeopatiaAstrologia.setPianetaVenere( segno );

            }else if( ite.getNumeroPianeta() == Constants.Pianeti.MARTE.getNumero() ) {
                omeopatiaAstrologia.setPianetaMarte( segno );

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

            if(segno.getElemento().getCode() == 0){
                totElementoFuoco = totElementoFuoco + 1;

            }else if(segno.getElemento().getCode() == 1){
                totElementoAcqua = totElementoAcqua + 1;

            }else if(segno.getElemento().getCode() == 2){
                totElementoTerra = totElementoTerra + 1;

            }else if(segno.getElemento().getCode() == 3){
                totElementoAria = totElementoAria + 1;
            }

        }

        omeopatiaAstrologia.setTotElementoFuoco( totElementoFuoco );
        omeopatiaAstrologia.setTotElementoAcqua( totElementoAcqua );
        omeopatiaAstrologia.setTotElementoTerra( totElementoTerra );
        omeopatiaAstrologia.setTotElementoAria( totElementoAria );



        // ############################ FINEEEEEEE ########################


        StringBuilder descTemaNatale = new StringBuilder();


        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Pianeti Segno Elemento</h4><br>");

        descTemaNatale.append("Ascendente in "+ omeopatiaAstrologia.getAscendente().getNomeSegnoZodiacale() +" Elemento: "
                + omeopatiaAstrologia.getAscendente().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(0).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaSole().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(1).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaLuna().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(2).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaMercurio().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(3).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaVenere().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(4).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaMarte().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(5).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaGiove().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(6).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaSaturno().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(7).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaUrano().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(8).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaNettuno().getElemento().getName()+"<br>");

        descTemaNatale.append(pianetiTransiti.get(9).descrizionePianeta() +" Elemento: "
                + omeopatiaAstrologia.getPianetaPlutone().getElemento().getName()+"<br>");


        descTemaNatale.append("<br>");
        descTemaNatale.append("Num totale elementi Fuoco: "+omeopatiaAstrologia.getTotElementoFuoco() +"<br>");
        descTemaNatale.append("Num totale elementi Acqua: "+omeopatiaAstrologia.getTotElementoAcqua() +"<br>");
        descTemaNatale.append("Num totale elementi Terra: "+omeopatiaAstrologia.getTotElementoTerra() +"<br>");
        descTemaNatale.append("Num totale elementi Aria: "+omeopatiaAstrologia.getTotElementoAria() );


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

