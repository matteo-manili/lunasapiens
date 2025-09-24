package com.lunasapiens.repository;

import org.postgresql.util.PGobject;

public class UtilsRepository {

    /**
     * Converte un array di Float in un oggetto PGobject compatibile con il tipo
     * vettoriale di PostgreSQL (pgvector).
     *
     * 🔹 Cosa fa il metodo:
     *   1️⃣ Trasforma l'array di Float in una stringa nel formato JSON-like
     *       richiesto da PGvector, ad esempio: [0.12,0.34,0.56].
     *   2️⃣ Crea un PGobject, che è la classe Java fornita dal driver PostgreSQL
     *       per rappresentare tipi SQL non standard (come 'vector').
     *   3️⃣ Imposta il tipo SQL su "vector" e assegna la stringa formattata come valore.
     *
     * 🔹 A cosa serve PGobject:
     *   - PGobject permette di mappare tipi PostgreSQL che non hanno un equivalente diretto in Java,
     *     come array, jsonb o tipi personalizzati come 'vector'.
     *   - Senza PGobject, il driver JDBC non saprebbe come gestire questo tipo durante
     *     l'inserimento nel database.
     *
     * 🔹 Uso tipico:
     *   Float[] embedding = {0.12f, 0.34f, 0.56f};
     *   PGobject pgVector = UtilsRepository.toPgVector(embedding);
     *   // poi puoi passare pgVector a JdbcTemplate o PreparedStatement
     *
     * @param embedding array di float da salvare come vettore PostgreSQL
     * @return PGobject pronto per essere salvato nel DB
     * @throws Exception in caso di errore nella creazione del PGobject
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
