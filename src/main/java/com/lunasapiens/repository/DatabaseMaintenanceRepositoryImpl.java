package com.lunasapiens.repository;

import com.lunasapiens.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Repository per operazioni di manutenzione sul database PostgreSQL.
 *
 * Differenze tra EntityManager e JdbcTemplate:
 *
 * - EntityManager (JPA):
 *   • Lavora a livello di oggetti e entità, mappando classi Java su tabelle database.
 *   • Gestisce il ciclo di vita delle entità, caching, lazy loading e transazioni.
 *   • Ideale per CRUD e query orientate agli oggetti.
 *   • Esegue normalmente tutte le operazioni dentro un blocco transazionale.
 *
 * - JdbcTemplate (Spring JDBC):
 *   • Permette di eseguire direttamente query SQL native, con gestione semplificata
 *     di connessioni e risorse.
 *   • Non usa ORM, lavora a basso livello con SQL e ResultSet.
 *   • Utile per operazioni che richiedono esecuzione fuori transazioni (es. comandi
 *     di manutenzione, operazioni batch, procedure SQL).
 *   • Più leggero e semplice per comandi speciali o query native.
 */

@Repository
public class DatabaseMaintenanceRepositoryImpl implements DatabaseMaintenanceRepository {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMaintenanceRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMaintenanceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * La colonna video della tabella oroscopo_giornaliero è di tipo OID (Object Identifier). Questo significa che invece di memorizzare direttamente i dati binari
     * (video) nella tabella, PostgreSQL conserva solo un identificatore (OID) che punta a un Large Object memorizzato esternamente nella tabella di sistema pg_largeobject.
     * Come funziona la correlazione con pg_largeobject:
     * Quando inserisci un video nel campo video come OID, PostgreSQL salva il contenuto binario del video in una o più righe della tabella pg_largeobject.
     * La tabella oroscopo_giornaliero memorizza solo l’OID che identifica univocamente quel Large Object.
     * pg_largeobject contiene quindi i dati effettivi dei video, suddivisi in pezzi, ognuno associato a un OID.
     * Nel metodo deleteOldOroscopoRecords:
     * Prima si recuperano gli OID dei video da cancellare (record con data_oroscopo vecchia).
     * Poi si chiama la funzione lo_unlink(oid), che rimuove completamente il Large Object corrispondente da pg_largeobject.
     * Infine si cancellano i record dalla tabella principale.
     * Perché è importante:
     * Se cancelli solo i record da oroscopo_giornaliero senza rimuovere i Large Object da pg_largeobject, lo spazio su disco occupato dai video non verrà liberato:
     * i dati resteranno “orfani” in pg_largeobject, causando bloat e spreco di spazio.
     * Usando lo_unlink(oid), si garantisce che i dati binari video siano rimossi correttamente dalla tabella di sistema pg_largeobject.
     *
     * Riassumendo:
     * La tabella oroscopo_giornaliero conserva solo riferimenti (OID) ai dati video reali che sono memorizzati in pg_largeobject. Il metodo elimina prima i dati video
     * fisici tramite lo_unlink su pg_largeobject e poi cancella i riferimenti dalla tabella principale, mantenendo il database pulito e senza sprechi di spazio.
     */
    @Override
    public void deleteOldOroscopoRecords() {
        // Converti LocalDateTime in java.sql.Timestamp
        LocalDateTime oggiRomaOre12 = Utils.OggiRomaOre12();
        java.sql.Timestamp oggiRomaOre12_SqlTimestamp = java.sql.Timestamp.valueOf(oggiRomaOre12);

        //Esegue una query che prende tutti i valori della colonna video (che contiene gli OID del Large Object) per tutte le righe con data_oroscopo precedente alla data limite.
        //oids è una lista degli identificatori OID (Long) dei Large Object associati ai video vecchi.
        String selectSql = "SELECT video FROM oroscopo_giornaliero WHERE data_oroscopo < ?";
        List<Long> oids = jdbcTemplate.queryForList(selectSql, Long.class, oggiRomaOre12_SqlTimestamp);

        //Per ogni OID in lista, esegue il comando SQL lo_unlink(oid).
        //lo_unlink è una funzione di PostgreSQL che elimina il Large Object corrispondente all’OID, liberando lo spazio occupato nel sistema pg_largeobject.
        for (Long oid : oids) {
            jdbcTemplate.execute("SELECT lo_unlink(" + oid + ")");
        }

        //Dopo aver eliminato i Large Object fisici, cancella tutte le righe dalla tabella che corrispondono ai dati oroscopo vecchi (data precedente alla data limite).
        String deleteSql = "DELETE FROM oroscopo_giornaliero WHERE data_oroscopo < ?";
        jdbcTemplate.update(deleteSql, oggiRomaOre12_SqlTimestamp);
    }



    /**
    Questo metodo esegue un comando VACUUM FULL sulla tabella oroscopo_giornaliero del database PostgreSQL.
    VACUUM FULL rimuove in modo completo le tuple obsolete causate da cancellazioni o aggiornamenti,
    ricompone fisicamente la tabella liberando spazio su disco,
    riduce la dimensione fisica della tabella,
    ma blocca la tabella durante l’operazione, impedendo letture e scritture fino al completamento.
    In pratica, questo metodo serve a ottimizzare e recuperare spazio disco sulla tabella oroscopo_giornaliero, migliorandone l’efficienza e riducendo il "bloat".
     */
    @Override
    public void vacuumFullOroscopoGiornaliero() {
        jdbcTemplate.execute("VACUUM FULL oroscopo_giornaliero");
    }

    /**
     * Questo metodo esegue il comando VACUUM FULL sull’intero database PostgreSQL.
     * VACUUM FULL pulisce completamente tutte le tabelle rimuovendo tuple obsolete dovute a cancellazioni o aggiornamenti,
     * ricompone fisicamente ogni tabella liberando spazio su disco,
     * riduce la dimensione fisica complessiva del database,
     * ma blocca ogni tabella mentre l’operazione è in corso, impedendo letture e scritture fino al termine.
     * In sintesi, questo metodo serve a ottimizzare e recuperare spazio fisico su disco in tutto il database, migliorandone le prestazioni e riducendo il bloat generale.
     */
    @Override
    public void vacuumFull() {
        jdbcTemplate.execute("VACUUM FULL");
    }

    /**
     * Questo metodo esegue il comando VACUUM standard su tutto il database PostgreSQL.
     * VACUUM pulisce le tuple obsolete generate da cancellazioni e aggiornamenti,
     * libera spazio interno nelle tabelle per futuri inserimenti senza ridurre la dimensione fisica su disco,
     * non blocca le tabelle durante l’esecuzione, quindi permette letture e scritture contemporaneamente,
     * aiuta a mantenere efficiente il database e previene il fenomeno del bloat.
     * In sintesi, questo metodo serve a effettuare una manutenzione leggera e non invasiva per mantenere il database performante e pulito.
     */
    @Override
    public void vacuum() {
        jdbcTemplate.execute("VACUUM");
    }



}
