package com.lunasapiens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DatabaseStatusController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseStatusController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/database/status")
    public String getDatabaseStatus() {
        try {
            // Esegui una query di test per verificare la connessione al database
            jdbcTemplate. queryForObject("SELECT 1", Integer.class);



            List<Map<String, Object>> rows = getTableData();

            // Cicla attraverso le righe e stampa i dati sulla console
            for (Map<String, Object> row : rows) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String columnName = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println(columnName + ": " + value);
                }
                System.out.println(); // Aggiungi una riga vuota tra le righe dei dati
            }


            return "Database is up and running!";
        } catch (Exception e) {
            // Gestisci eventuali eccezioni se la connessione al database fallisce
            return "Database connection error: " + e.getMessage();
        }
    }



        public List<Map<String, Object>> getTableData() {
            String sql = "SELECT * FROM tutorials"; // Sostituisci "your_table" con il nome della tua tabella

            // Esegui la query e ottieni i risultati come lista di mappe
            return jdbcTemplate.queryForList(sql);
        }




    //-----------------------


    //private final TutorialRepository tutorialRepository;

/*
    @GetMapping("/tutorials")
    public void getTutorials() {
        List<Tutorials> tutorials = tutorialRepository.findAll();

        for (Tutorials tutorial : tutorials) {
            System.out.println("ID: " + tutorial.getId());
            System.out.println("Title: " + tutorial.getTitle());
            System.out.println("Description: " + tutorial.getDescription());
            System.out.println("---------------------------");
        }
    }


*/





}

