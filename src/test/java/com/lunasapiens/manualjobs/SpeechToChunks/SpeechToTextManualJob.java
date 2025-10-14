package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;

import com.lunasapiens.entity.VideoChunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.PunteggiaturaIAService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.FaiSintesiIAService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.AudioTranscriptionService;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.repository.VideoChunksRepository;
import com.lunasapiens.service.TextEmbeddingHuggingfaceService;
import com.lunasapiens.manualjobs.ArticleEmbedding.service.TextEmbeddingService;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SpeechToTextManualJob {

    @Autowired
    private AudioTranscriptionService audioTranscriptionService;

    @Autowired
    private PunteggiaturaIAService punteggiaturaIAService;

    @Autowired
    private FaiSintesiIAService faiSintesiIAService;

    @Autowired
    TextEmbeddingHuggingfaceService textEmbeddingHuggingfaceService;

    @Autowired
    private VideoChunksRepository videoChunksRepository;

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;

    @Autowired
    com.lunasapiens.service.RAGIAService RAGIAService;



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeech_DIVIDI_IN_CHUNKS() throws Exception {
        List<VideoChunks> list = videoChunksRepository.findAll()
                .stream()
                //.filter(vc -> vc.getNumeroVideo() >= 175 && vc.getNumeroVideo() <= 192)
                .toList();
        for(VideoChunks videoChunks: list){
            // 4️⃣ Dividi il testo in chunk
            //List<String> chunks = dividiTestoInChunk(videoChunks.getFullContent(), 500, 80); // 200 parole per chunk, overlap 40 parole

            List<String> chunks = new ArrayList<>(List.of( videoChunks.getSintesi() ));

            //List<String> chunks = dividiTestoInChunk(TESTO_PUNTEGGIATO_117.toString());
            // 5️⃣ Salva i chunk nel database calcolando embedding reale
            salvaChunkInDatabase(chunks, videoChunks);
        }
    }





    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_FAI_SINTESI() throws Exception {
        List<VideoChunks> list = videoChunksRepository.findAll()
                .stream()
                //.filter(vc -> vc.getNumeroVideo() >= 175 && vc.getNumeroVideo() <= 192)
                .toList();
        for(VideoChunks videoChunks: list){
            try {
                int numneroTotaleParole = countWords(videoChunks.getFullContent());

                StringBuilder sintesi = faiSintesiIAService.generaSintesi(videoChunks.getFullContent(), numneroTotaleParole);

                videoChunks.setSintesi( sintesi.toString() );

                videoChunksRepository.save(videoChunks); // salva nel DB

            } catch (Exception e) {
                // altre eccezioni impreviste
                System.err.println("❌ Errore durante l’elaborazione di VIDEO_TITLE=" + videoChunks.getTitle());
                e.printStackTrace();
            }
        }
    }




    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_VIDEO_MULTIPLO() throws Exception {
        for(int numVideo = 33; numVideo <= 169; numVideo++ ){
            Long VIDEO_ID = Long.valueOf(numVideo);
            System.out.println("########## VIDEO "+String.valueOf(VIDEO_ID)+" ##########");
            try {
                // 1️⃣ Trascrivi audio
                String trascrizioneAudio = transcribeAudioFile( "src/test/resources/video_tupini/"+String.valueOf(VIDEO_ID)+".wav" );

                // 2️⃣ Ripristina punteggiatura
                int numneroTotaleParole = countWords(trascrizioneAudio);
                System.out.println("numneroParole: "+numneroTotaleParole);
                StringBuilder testoPunteggiato = punteggiaturaIAService.generaTestoConPunteggiatura(trascrizioneAudio, numneroTotaleParole);
                //StringBuilder testoPunteggiato = punteggiaturaIAService.generaTestoConPunteggiatura(trascrizioneAudio_117, numneroTotaleParole);

                // 3️⃣ Crea VideoChunks con title = numero del video e fullContent = testo punteggiato
                VideoChunks videoChunks = new VideoChunks();
                videoChunks.setNumeroVideo(VIDEO_ID);
                videoChunks.setTitle(String.valueOf(VIDEO_ID));
                videoChunks.setFullContent(testoPunteggiato.toString());
                videoChunks.setMetadati(Map.of()); // JSON vuoto {}

                videoChunks = videoChunksRepository.save(videoChunks); // salva nel DB

            } catch (java.io.FileNotFoundException e) {
                // Log e continua con il prossimo video
                System.err.println("⚠️ File non trovato per VIDEO_ID=" + VIDEO_ID + ": " + e.getMessage());
                continue;
            } catch (Exception e) {
                // altre eccezioni impreviste
                System.err.println("❌ Errore durante l’elaborazione di VIDEO_ID=" + VIDEO_ID);
                e.printStackTrace();
            }
        }
    }




    public void salvaChunkInDatabase(List<String> chunks, VideoChunks videoChunks){
        int chunkIndex = 1;
        for (String chunkContent : chunks) {
            try {
                // Calcola embedding reale tramite TextEmbeddingService

                //Float[] embedding = textEmbeddingService.computeCleanEmbedding(chunkContent);

                Float[] embedding = textEmbeddingHuggingfaceService.computeCleanEmbedding(chunkContent);

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
        File audioFile = new File( filePath );
        // Trascrizione del file audio
        String transcription = audioTranscriptionService.transcribeAudio(audioFile);
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
        return words.length;
    }



}
