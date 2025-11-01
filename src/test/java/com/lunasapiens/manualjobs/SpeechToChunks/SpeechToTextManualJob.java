package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;

import com.lunasapiens.entity.VideoChunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.AudioTranscriptionMultiThredService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.PunteggiaturaIAService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.FaiSintesiIAService;

import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.repository.VideoChunksRepository;
import com.lunasapiens.service.HuggingfaceTextEmbedding_E5LargeService;

import com.lunasapiens.service.HuggingfaceTextSummarizationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SpeechToTextManualJob {

    @Autowired
    private AudioTranscriptionMultiThredService audioTranscriptionMultiThredService;

    @Autowired
    private PunteggiaturaIAService punteggiaturaIAService;

    @Autowired
    private FaiSintesiIAService faiSintesiIAService;

    @Autowired
    private HuggingfaceTextEmbedding_E5LargeService textEmbedding;

    @Autowired
    private HuggingfaceTextSummarizationService summarizationService;

    @Autowired
    private VideoChunksRepository videoChunksRepository;

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;




    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_TEST_SINTESI_E_TITOLO_ARTICOLO() throws Exception {
        String longText = articolo; // anche 1500 parole
        String title = summarizationService.summarize(longText, true);
        String summary = summarizationService.summarize(longText, false);

        System.out.println("TITOLO: " + title);
        System.out.println("SOMMARIO: " + summary);
    }



    private static String articolo = "Considerazioni sociali riguardo i fratelli Ramponi\n" +
            "\n" +
            "Franco Ramponi, Dino Ramponi e Maria Luisa Ramponi\n" +
            "\n" +
            "Ecco una panoramica aggiornata – con considerazioni sociali – riguardo alla vicenda dei fratelli Ramponi, che si è sviluppata negli ultimi anni a Castel d’Azzano (Verona).\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "Contesto della vicenda\n" +
            "\n" +
            "I tre fratelli, Franco, Dino e Maria Luisa Ramponi, gestivano fino a tempi recenti una piccola azienda agricola/famigliare con allevamento e campi, ereditata dai genitori.\n" +
            "\n" +
            "A seguito di un incidente stradale nel 2012 (un trattore con fari spenti guidato da uno dei fratelli che causò la morte di un uomo) la vicenda ha preso una piega giudiziaria ed economica drammatica.\n" +
            "\n" +
            "Nel tempo si accumularono debiti, ipoteche, pignoramenti: il casolare, i terreni, gli animali sono entrati in procedure di esecuzione.\n" +
            "\n" +
            "I fratelli vissero in condizioni isolate e che i media definiscono «di fatto medievali»: senza luce elettrica, vivendo di notte, allevamento notturno per evitare sguardi, pochi contatti con il mondo esterno.\n" +
            "\n" +
            "Alla fine la situazione è culminata in un’esplosione del casolare, conseguente allo sgombero: tre carabinieri sono morti, numerosi feriti, e i fratelli sono stati arrestati.\n" +
            "\n" +
            " \n" +
            "\n" +
            "---\n" +
            "\n" +
            "Considerazioni sociali principali\n" +
            "\n" +
            "1. Isolamento sociale e marginalità\n" +
            "L’esperienza dei Ramponi evidenzia come, pur in un contesto rurale relativamente vicino a centri abitati, una famiglia possa trovarsi a vivere in uno stato di forte isolamento: rifiuto dei contatti con i servizi sociali, assenza di rete, barricamento sociale. \n" +
            "Questo aiuta a comprendere come condizioni di vulnerabilità (economica + culturale) possano degenerare quando non viene attivata alcuna mediazione efficace.\n" +
            "\n" +
            "\n" +
            "2. Crisi dell’agricoltura familiare come sfondo\n" +
            "La vicenda non è semplicemente personale: è inserita in un contesto più ampio in cui molte aziende a conduzione famigliare in Veneto (e non solo) sono chiuse o in difficoltà. \n" +
            "Lo sfruttamento di mutui, ipoteche, la dipendenza da creditori esterni, la perdita della proprietà tradizionale, rappresentano una dinamica più ampia di precarietà rurale.\n" +
            "\n" +
            "\n" +
            "3. Rapporto tra cittadini e istituzioni, fiducia e conflitto\n" +
            "I Ramponi hanno più volte dichiarato di sentirsi vittime di un sistema ingiusto, denunciando firme false, procedure di credito scorrette, ecc. \n" +
            "Allo stesso tempo, le istituzioni locali (Comune, servizi sociali, tribunale) hanno cercato soluzioni alternative (abitazioni sostitutive, assistenza) che i fratelli hanno rifiutato. \n" +
            "Questo conflitto – rifiuto di aiuto + percezione di ingiustizia – ha contribuito all’escalation.\n" +
            "\n" +
            "\n" +
            "4. Estremo gesto come esito della disperazione/contesto\n" +
            "L’atto dell’esplosione del casolare – saturato di gas, uso di molotov, modalità di barricamento – va letto anche come espressione estrema di un conflitto in cui la famiglia non aveva – o percepiva di non avere – altre “uscite”. \n" +
            "Socialmente, questo apre riflessioni su come condizioni di esclusione, marginalizzazione, perdita di contatto con la comunità possano generare gesti radicali, violenti.\n" +
            "\n" +
            "\n" +
            "5. Precarietà culturale e formativa\n" +
            "I media parlano di “estrema indigenza socio-culturale” dei fratelli, difficoltà nel relazionarsi, conoscenza limitata delle “regole del gioco”, del mondo istituzionale. \n" +
            "Ciò suggerisce che non sia stata solo una questione economica, ma anche di capitale sociale, educazione, supporto comunitario.\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "Implicazioni e spunti di riflessione\n" +
            "\n" +
            "Prevenzione: la vicenda mostra che l’intervento precoce – sociale, comunitario, psicologico – in famiglie rurali in difficoltà può essere cruciale.\n" +
            "\n" +
            "Reti di supporto: nei contesti agricoli, le reti tradizionali (famiglia, vicinato, associazioni agricole) possono deteriorarsi; occorre pensare nuovi modelli.\n" +
            "\n" +
            "Diritto alla proprietà e stabilità: la perdita della casa dei genitori, della terra di famiglia, rappresenta un trauma identitario oltre che economico.\n" +
            "\n" +
            "Ruolo delle istituzioni: il conflitto tra volontà della famiglia e imposizione di azioni esecutive (sgombero, pignoramento) porta a tensioni gravissime; serve un equilibrio tra diritti del debitore, tutela collettiva e sistema dell’esecuzione.\n" +
            "\n" +
            "Cultura rurale vs modernità: il caso evidenzia uno scontro tra modelli di vita “antichi” (autarchia, autogestione rurale, isolamento) e le esigenze/controlli della modernità (credito, normativa, relazioni sociali).";


    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_CREA_CHUNKS() {
        for (int numVideo = 1; numVideo <= 192; numVideo++) {
            final long NUM_VIDEO = numVideo; // deve essere final per usarlo nella lambda
            VideoChunks video = videoChunksRepository.findByNumeroVideo(NUM_VIDEO).orElse(null);
            if (video != null && video.getSintesi() != null) {
                System.out.println("✅ Trovato video con numeroVideo = " + video.getNumeroVideo());
                System.out.println("Titolo: " + video.getTitle());
                try {
                    // ############################## CREA CHUNKS ##############################
                    // ESEMPIO // List<String> chunks = dividiTestoInChunk(videoChunks.getFullContent(), 500, 80); // 200 parole per chunk, overlap 40 parole
                    List<String> chunks = new ArrayList<>(List.of( video.getSintesi() ));
                    // List<String> chunks = dividiTestoInChunk(TESTO_PUNTEGGIATO_117.toString());
                    // Salva i chunk nel database calcolando embedding reale
                    salvaChunkInDatabase(chunks, video);

                } catch (Exception e) {
                    System.err.println("❌ Errore durante L'EMBEDDING del NUM_VIDEO=" + NUM_VIDEO);
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Nessun video trovato con numeroVideo = " + NUM_VIDEO);
            }
        }
    }



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_VIDEO_PUNTEGGIATURA_E_SINTESI() {
        for (int numVideo = 1; numVideo <= 192; numVideo++) {
            final long NUM_VIDEO = numVideo; // deve essere final per usarlo nella lambda
            VideoChunks video = videoChunksRepository.findByNumeroVideo(NUM_VIDEO).orElse(null);
            if (video != null && video.getSintesi() == null) {
                System.out.println("✅ Trovato video con numeroVideo = " + video.getNumeroVideo());
                System.out.println("Titolo: " + video.getTitle());
                try {
                    // ############################## FAI PUNTEGGIATURA E SINTESI ##############################
                    if( video.getOriginal() != null ){
                        String testoOriginale = video.getOriginal();
                        StringBuilder testoPunteggiato = punteggiaturaIAService.generaTestoConPunteggiatura(testoOriginale, countWords(testoOriginale));
                        video.setFullContent(testoPunteggiato.toString());
                        StringBuilder sintesi = faiSintesiIAService.generaSintesi(testoPunteggiato.toString(), countWords(testoPunteggiato.toString()));
                        video.setSintesi( sintesi.toString() );
                        videoChunksRepository.save(video);

                    }else if( video.getFullContent() != null){
                        String testoPunteggiato = video.getFullContent();
                        StringBuilder sintesi = faiSintesiIAService.generaSintesi(testoPunteggiato, countWords(testoPunteggiato));
                        video.setSintesi( sintesi.toString() );
                        videoChunksRepository.save(video);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Errore durante l’elaborazione testi del NUM_VIDEO=" + NUM_VIDEO);
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Nessun video trovato con numeroVideo = " + NUM_VIDEO);
            }

        }
    }





    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_VIDEO_AUDIO_MULTI_THREAD() throws Exception {
        // ✅ Thread pool con 3 thread (3 video contemporaneamente)
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<?>> futures = new ArrayList<>();

        for (int numVideo = 60; numVideo <= 60; numVideo++) {
            final long NUM_VIDEO = numVideo; // deve essere final per usarlo nella lambda
            // Invia ogni video come task al thread pool
            futures.add(executor.submit(() -> {
                System.out.println("########## VIDEO " + NUM_VIDEO + " ##########");
                try {
                    VideoChunks video = videoChunksRepository.findByNumeroVideo(NUM_VIDEO).orElse(null);
                    if( (video != null && video.getOriginal() == null) || video == null) {
                        // 1️⃣ Trascrivi audio
                        String videoPath = "src/test/resources/video_tupini/" + NUM_VIDEO + ".wav";
                        String trascrizioneAudio = transcribeAudioFile(videoPath);
                        if (video != null) {
                            video.setOriginal(trascrizioneAudio);
                            video.setMetadati(Map.of()); // JSON vuoto {}, non accetta null
                            videoChunksRepository.save(video);
                        }else {
                            // 2️⃣ Crea VideoChunks
                            VideoChunks newVideoChunks = new VideoChunks();
                            newVideoChunks.setNumeroVideo(NUM_VIDEO);
                            newVideoChunks.setTitle(String.valueOf(NUM_VIDEO));
                            newVideoChunks.setOriginal(trascrizioneAudio);
                            newVideoChunks.setMetadati(Map.of()); // JSON vuoto {}, non accetta null
                            videoChunksRepository.save(newVideoChunks);
                        }
                    }
                } catch (java.io.FileNotFoundException e) {
                    System.err.println("⚠️ File non trovato per VIDEO_ID=" + NUM_VIDEO + ": " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("❌ Errore durante l’elaborazione di VIDEO_ID=" + NUM_VIDEO);
                    e.printStackTrace();
                }
            }));
        }
        // ⏳ Aspetta che tutti i task finiscano
        for (Future<?> f : futures) {
            try {
                f.get(); // blocca finché ogni task termina (o lancia eccezione)
            } catch (Exception e) {
                System.err.println("❌ Errore in uno dei thread: " + e.getMessage());
            }
        }

        // 🧹 Chiude il pool
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS); // o un tempo adeguato

    }


    public void salvaChunkInDatabase(List<String> chunks, VideoChunks videoChunks){
        int chunkIndex = 1;
        for (String chunkContent : chunks) {
            try {
                // Calcola embedding reale tramite TextEmbeddingService

                //Float[] embedding = textEmbeddingService.computeCleanEmbedding(chunkContent);

                Float[] embedding = textEmbedding.embedDocument(chunkContent);

                // Salva il chunk nel DB usando il repository custom
                Chunks savedChunk = chunksCustomRepository.saveChunkJdbc(videoChunks, chunkIndex, chunkContent, embedding);

                System.out.println("Chunk salvato con ID: " + savedChunk.getId());

            } catch (Exception e) {
                System.err.println("Errore durante salvataggio chunk " + chunkIndex + " del video " + videoChunks.getId());
                e.printStackTrace();
            }
            chunkIndex++;
        }
        System.out.println("Caricamento chunks completato: " + chunks.size() + " chunk salvati.");
    }




    public List<String> dividiTestoInChunk(String testo, int chunkSize, int overlap) throws  Exception {
        //testo = "Buonasera a tutti! Mi chiamo Gabriella Tupini. Sto facendo dei video per comunicare alle persone interessate";
        List<String> chunks = chunkText_con_Overlap(testo, chunkSize, overlap);
        int index = 1;
        for (String c : chunks) {
            System.out.println("Chunk " + index++ + ":\n" + c + "\n---");
        }
        return chunks;
    }



    /**
     * Divide un testo in chunk da ~N parole, rispettando frasi e un overlap fisso (in parole).
     *
     * @param text       Il testo da suddividere
     * @param chunkSize  Dimensione massima del chunk in parole
     * @param overlap    Numero fisso di parole da sovrapporre tra i chunk
     * @return Lista di chunk di testo
     */
    public static List<String> chunkText_con_Overlap(String text, int chunkSize, int overlap) {
        // Normalizza \n e spazi multipli
        text = text.replaceAll("\\s+", " ").trim();

        // Suddivide in frasi usando punteggiatura (.!?)
        String[] sentences = text.split("(?<=[.!?])\\s+");

        List<String> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int wordCount = 0;

        for (String sentence : sentences) {
            String[] words = sentence.split("\\s+");
            int sentenceWordCount = words.length;

            // Se aggiungendo questa frase supero chunkSize, creo un chunk
            if (wordCount + sentenceWordCount > chunkSize && !currentChunk.isEmpty()) {
                // Crea chunk
                String chunk = String.join(" ", currentChunk);
                chunks.add(chunk);

                // Sliding window: mantieni le ultime parole per overlap
                List<String> newChunk = new ArrayList<>();
                int overlapWords = 0;
                for (int i = currentChunk.size() - 1; i >= 0 && overlapWords < overlap; i--) {
                    String[] w = currentChunk.get(i).split("\\s+");
                    overlapWords += w.length;
                    newChunk.add(0, currentChunk.get(i));
                }

                currentChunk = newChunk;
                wordCount = overlapWords;
            }

            currentChunk.add(sentence);
            wordCount += sentenceWordCount;
        }

        // Aggiungi l’ultimo chunk se non vuoto
        if (!currentChunk.isEmpty()) {
            chunks.add(String.join(" ", currentChunk));
        }
        return chunks;
    }






    /**
     * Divide un testo in chunk da ~400 parole, rispettando frasi e overlap del 15%
     */
    public static List<String> chunkText_con_Overlap(String text, int chunkSize) {
        int overlap = (int) (chunkSize * 0.15);
        // Normalizza \n e spazi multipli
        text = text.replaceAll("\\s+", " ").trim();
        // Suddivide in frasi usando punteggiatura (.!?)
        String[] sentences = text.split("(?<=[.!?])\\s+");
        List<String> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int wordCount = 0;
        for (String sentence : sentences) {
            String[] words = sentence.split("\\s+");
            int sentenceWordCount = words.length;
            if (wordCount + sentenceWordCount > chunkSize && !currentChunk.isEmpty()) {
                // Crea chunk
                String chunk = String.join(" ", currentChunk);
                chunks.add(chunk);
                // Sliding window: mantieni le ultime parole per overlap
                List<String> newChunk = new ArrayList<>();
                int overlapWords = 0;
                for (int i = currentChunk.size() - 1; i >= 0 && overlapWords < overlap; i--) {
                    String[] w = currentChunk.get(i).split("\\s+");
                    overlapWords += w.length;
                    newChunk.add(0, currentChunk.get(i));
                }
                currentChunk = newChunk;
                wordCount = overlapWords;
            }
            currentChunk.add(sentence);
            wordCount += sentenceWordCount;
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(String.join(" ", currentChunk));
        }
        return chunks;
    }





    /**
     * Divide un testo in chunk da ~400 parole, senza overlap
     */
    public static List<String> chunkText_senza_Overlap(String text) {
        int chunkSize = 400;

        // Normalizza \n e spazi multipli
        text = text.replaceAll("\\s+", " ").trim();

        // Suddivide in frasi usando punteggiatura (.!?)
        String[] sentences = text.split("(?<=[.!?])\\s+");

        List<String> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int wordCount = 0;

        for (String sentence : sentences) {
            String[] words = sentence.split("\\s+");
            int sentenceWordCount = words.length;
            if (wordCount + sentenceWordCount > chunkSize && !currentChunk.isEmpty()) {
                // Crea chunk
                chunks.add(String.join(" ", currentChunk));
                // Inizia un nuovo chunk senza overlap
                currentChunk = new ArrayList<>();
                wordCount = 0;
            }
            currentChunk.add(sentence);
            wordCount += sentenceWordCount;
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(String.join(" ", currentChunk));
        }

        return chunks;
    }



    public String transcribeAudioFile(String filePath) throws Exception {
        // Percorso del file audio nella cartella resources
        //Formato: WAV
        //Bit depth: 16 bit
        //Canali: Mono
        //Sample rate: 16 kHz o 16000 Hz
        File audioFile = new File(filePath);
        // Trascrizione del file audio
        String transcription = audioTranscriptionMultiThredService.transcribeAudio(audioFile);

        //String transcription = null;
        // Stampa il testo trascritto
        System.out.println("Testo trascritto:");
        System.out.println(transcription);
        return transcription;
    }




    /**
     * Conta il numero di parole in una stringa.
     *
     * 🔹 Una "parola" è definita come sequenza di caratteri separata da spazi bianchi.
     *
     * @param text stringa di input
     * @return numero di parole trovate
     */
    public static int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0; // nessuna parola
        }
        // Divide la stringa su uno o più spazi bianchi
        String[] words = text.trim().split("\\s+");

        System.out.println("numeroParole: " + words.length);
        return words.length;
    }



}
