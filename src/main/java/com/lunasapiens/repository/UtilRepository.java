package com.lunasapiens.repository;

import org.postgresql.util.PGobject;

public class UtilRepository {

    /**
     * Converte un array di float in un PGobject compatibile con PGvector.
     */
    public static PGobject toPgVector(Float[] embedding) throws Exception {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");

        PGobject pgVector = new PGobject();
        pgVector.setType("vector");
        pgVector.setValue(sb.toString());
        return pgVector;
    }




}
