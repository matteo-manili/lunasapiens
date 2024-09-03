package com.lunasapiens.zodiac;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ZodiacUtils {

    private static final Logger logger = LoggerFactory.getLogger(ZodiacUtils.class);




    // ###################################### UTILS ######################################

    public static void assegnaCaseAiPianeti(List<Pianeta> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {
        // Creare una copia della lista delle case per ordinarla
        List<CasePlacide> caseOrdinate = new ArrayList<>(casePlacideArrayList);
        caseOrdinate.sort(Comparator.comparingDouble(CasePlacide::getGradi));

        for (Pianeta pianeta : pianetiTransiti) {
            double gradiPianeta = pianeta.getGradi();
            for (int i = 0; i < caseOrdinate.size(); i++) {
                CasePlacide casaCorrente = caseOrdinate.get(i);
                CasePlacide casaSuccessiva = caseOrdinate.get((i + 1) % caseOrdinate.size());

                // Controllare se il pianeta è tra la casa corrente e la successiva
                if (casaCorrente.getGradi() < casaSuccessiva.getGradi()) {
                    if (gradiPianeta >= casaCorrente.getGradi() && gradiPianeta < casaSuccessiva.getGradi()) {
                        pianeta.setNomeCasa(casaCorrente.getNomeCasa());
                        break;
                    }
                } else {
                    // Caso particolare in cui i gradi attraversano il punto zero
                    if (gradiPianeta >= casaCorrente.getGradi() || gradiPianeta < casaSuccessiva.getGradi()) {
                        pianeta.setNomeCasa(casaCorrente.getNomeCasa());
                        break;
                    }
                }
            }
        }
    }


}
