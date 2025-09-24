package com.lunasapiens.manualjobs.SpeechToText;

import com.lunasapiens.entity.Chunks;

import com.lunasapiens.manualjobs.SpeechToText.service.PunteggiaturaTestoIAService;
import com.lunasapiens.manualjobs.SpeechToText.service.SpeechToTextService;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.service.ArticleEmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpeechToTextManualJob {

    @Autowired
    private SpeechToTextService speechToTextService;

    @Autowired
    private PunteggiaturaTestoIAService punteggiaturaTestoIAService;

    @Autowired
    private ArticleEmbeddingService articleEmbeddingService;

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;




    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks() throws Exception {

        // 1️⃣ Trascrivi audio
        //String trascrizioneAudio = transcribeAudioFile();

        // 2️⃣ Ripristina punteggiatura
        //StringBuilder testoPunteggiato = punteggiaturaTestoIAService.punteggiaturaTesto(trascrizioneAudio);

        // 3️⃣ Dividi il testo in chunk
        //List<String> chunks = dividiTestoInChunk(testoPunteggiato.toString());
        List<String> chunks = dividiTestoInChunk(TESTO_PUNTEGGIATO_1.toString());

        // 4️⃣ Salva i chunk nel database calcolando embedding reale
        salvaChunkInDatabase(chunks);


    }



    public void salvaChunkInDatabase(List<String> chunks){
        Long videoId = 1L;  // ID del video
        int chunkIndex = 1;
        for (String chunkContent : chunks) {
            try {
                // Calcola embedding reale tramite ArticleEmbeddingService
                Float[] embedding = articleEmbeddingService.cleanTextEmbeddingPredictor(chunkContent);

                // Salva il chunk nel DB usando il repository custom
                Chunks savedChunk = chunksCustomRepository.saveChunkJdbc(videoId, chunkIndex, chunkContent, embedding);

                System.out.println("Chunk salvato con ID: " + savedChunk.getId());

            } catch (Exception e) {
                System.err.println("Errore durante salvataggio chunk " + chunkIndex + " del video " + videoId);
                e.printStackTrace();
            }
            chunkIndex++;
        }
        System.out.println("Caricamento chunks completato: " + chunks.size() + " chunk salvati.");
    }





    public List<String> dividiTestoInChunk(String testo) throws  Exception {

        //testo = "Buonasera a tutti! Mi chiamo Gabriella Tupini. Sto facendo dei video per comunicare alle persone interessate";

        List<String> chunks = chunkText(testo);

        int index = 1;
        for (String c : chunks) {
            System.out.println("Chunk " + index++ + ":\n" + c + "\n---");
        }

        return chunks;
    }


    /**
     * Divide un testo in chunk da ~400 parole, rispettando frasi e overlap del 15%
     */
    public static List<String> chunkText(String text) {
        int chunkSize = 400;
        int overlap = (int) (chunkSize * 0.15);
        int step = chunkSize - overlap;

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



    public String transcribeAudioFile() throws Exception {

        // Percorso del file audio nella cartella resources
        //Formato: WAV
        //Bit depth: 16 bit
        //Canali: Mono
        //Sample rate: 16 kHz o 16000 Hz
        File audioFile = new File("src/test/resources/models/vosk-model-it-0.22/AAA-file-audio/sample.wav");

        // Trascrizione del file audio
        String transcription = speechToTextService.transcribeAudio(audioFile);
        //String transcription = null;

        // Stampa il testo trascritto
        System.out.println("Testo trascritto:");
        System.out.println(transcription);

        return transcription;
    }



    private StringBuilder TESTO_PUNTEGGIATO_1 = new StringBuilder("Buonasera a tutti! Mi chiamo Gabriella Tupini. Sto facendo dei video per comunicare alle persone interessate il mio percorso spirituale e il percorso di una vita. È un percorso un po' anomalo, nel senso che non ha niente di idilliaco, niente di elegiaco, niente di soprannaturale. È stato un percorso abbastanza sofferto, come quello di tutti, però ha dato buoni frutti. \n" +
            "\n" +
            "Per capire cosa si intende per un percorso spirituale, non parlo di un percorso religioso, tutt'altro che religioso, ma di un percorso con i piedi per terra. Non parlo di un percorso razionale, cioè non della mente, ma di un percorso dell'anima. E allora occorre un po' specificare cosa intendo per anima, per mente e anche per istinto. \n" +
            "\n" +
            "Per anima intendo tutto ciò che si agita dentro di noi, tutte le emozioni e tutto ciò che ci rende svegli, nel bene o nel male. Per la mente, è quella che sta qua. Noi ne abbiamo due tipi di mente: uno ce lo danno alla nascita, ovvero è qualcosa che è suscettibile di diventare una mente; l'altro ce l'ho dall'ambiente che ci circonda ed è una serie di fatti, opinioni, idee, insegnamenti che ci provocano un condizionamento. \n" +
            "\n" +
            "Allora, il principio è questo: noi siamo tutti condizionati. Se non accettiamo questo, non andiamo avanti. Per andare avanti nel nostro percorso, bisogna accorgerci dei nostri ostacoli. Ce ne sono alcuni che sono uguali per tutti, ce ne sono altri che sono personali. Chi ci condiziona? Ci condiziona l'ambiente in cui viviamo, a cominciare dai genitori. Ci condizionano finché siamo in fasce, cioè da quando siamo in fasce. Non lo fanno volontariamente e, anzi, cercano di farlo in genere per il nostro bene, però ci condizionano trasmettendoci i loro condizionamenti. In più, ci condizionano i parenti; in più, è molto più fortemente che ci condiziona la scuola; in più, ci condizionano i libri, i giornali, la televisione, la politica, la religione. La religione ci condiziona fortemente. \n" +
            "\n" +
            "Con tutti questi condizionamenti, noi perdiamo la capacità di discernere e quindi di fare scelte opportune. Noi siamo tutti condizionati, lo ripeto. Come si fa a perdere il condizionarsi? Di condizionarsi vuol dire liberarsi. La libertà non è la libertà di fare il comodo proprio; la libertà sta qua dentro. È qui dentro che noi abbiamo due parti di noi che vanno in senso opposto: la mente solitamente va da una parte e l'anima va dall'altra. \n" +
            "\n" +
            "Facciamo un esempio: \"Eh, ma perché non ho risposto bene a quella persona? Perché non l'ho rimesso a posto quando quello mi ha trattato male? O perché non ho saputo dare una risposta brillante? O perché sono rimasta senza parole? Perché quando sono con un ragazzo o con una ragazza non mi viene niente di interessante da dire? Taccio. O quando sono davanti al capo, al capoufficio?\". \n" +
            "\n" +
            "Perché la nostra mente va da una parte e l'anima va dall'altra? Perché la mente si blocca. Attenzione: non è che essere se stessi sia la soluzione di tutto, come falsamente fanno supporre. Se noi ci lasciamo andare, noi possiamo, per esempio, essere così arrabbiati da prendere a male parole tutti gli altri, e gli altri agirebbero giustamente male contro di noi. Prima di lasciarsi andare con la nostra anima, bisogna conoscerla, bisogna capire cosa ama e cosa odia. \n" +
            "\n" +
            "Per poterlo fare, non dobbiamo bloccare nell'amore e nell'odio; non dobbiamo bloccarlo con i condizionamenti che ci hanno dato, non dobbiamo bloccarlo attraverso i pregiudizi. Non dobbiamo giudicarci, dobbiamo osservarci, ma non giudicarci. Noi siamo dentro una prigione; la nostra mente è una prigione. Per liberarsi, bisogna accorgersi che siamo in una prigione. \n" +
            "\n" +
            "Questa via spirituale è stata tentata nelle varie epoche da varie forme di pensiero, da varie filosofie, da varie organizzazioni, da vari individui. Per esempio, anticamente c'erano i sacri misteri. Perché non era contento della propria religione, cioè non gli bastava, voleva sapere altro, voleva andare al di fuori. Si scriveva ai sacri misteri; se riusciva a portarli fino in fondo, liberava la sua anima. O almeno, così ci dicono. Io ho forti dubbi su questo, e cioè non sui sacri misteri, ma sul fatto che molta gente potesse portarli a compimento. \n" +
            "\n" +
            "Però quello che si dice di coloro che l'avevano compiuto è che non avevano più paura di morire. Perché qualcuno ha commentato: \"Se non avevano paura di morire, perché gli avevano promesso un al di là?\". No, perché i cristiani, gli arabi e tutti gli altri che hanno religioni hanno una paura fregata di morire lo stesso. Tant'è vero che se vanno sempre raccomandati in chiesa. \n" +
            "\n" +
            "Però gli antichi usavano dei sistemi che oggi non potremmo usare, perché erano meno mentalizzati di noi e quindi lasciavano più spazio alle emozioni, ai simboli. Un simbolo dentro di loro poteva risvegliare una forte emozione. Noi, se vediamo un simbolo, di solito domandiamo: \"Che significa?\". Cioè non lasciamo che entri dentro di noi, perché la nostra mente lo blocca. \n" +
            "\n" +
            "Gli animali e i bambini sono molto meno mentalizzati di noi e capiscono molto di più sotto certi aspetti. Poi i bambini vanno a scuola, o gli animali, se sono domestici, vengono educati giustamente, però perdono una parte di quella istintualità. I bambini sentono lo stato d'animo dei loro genitori, poi vanno a scuola a cinque o sei anni e perdono tutto; non ci capiscono più niente. Per acquisirlo, ce ne vuole parecchio. I bambini sentono i cuori degli altri. \n" +
            "\n" +
            "L'istinto è la capacità di sentire, non di pensare, ma di sentire. Qui, quando abbiamo una persona di fronte, noi possiamo sentire come questa persona. Lo sentiamo da quello che ci rimanda. Se sentiamo una sensazione piacevole o spiacevole, per esempio, se pensiamo: \"Invece cominciava a dedurre, dire: è vestito così, quindi vuol dire che somiglia a quella persona, quindi vuol dire che ha detto queste cose, quindi vuol dire che è conquistare\". \n" +
            "\n" +
            "Questo istinto, riconquistare questo istinto, non è facile, però fa parte del cammino spirituale. Perché nel cammino spirituale autentico si concilia l'anima con la mente, l'anima con il corpo. Noi veniamo da una religione che ci mortifica il corpo, che ci dice che il corpo, siccome muore, non vale niente; quello che vale è l'anima immortale. Non è questo il cammino spirituale; questo è mentale, perché è basato sull'immaginazione e non sulla sperimentazione. \n" +
            "\n" +
            "Noi dobbiamo sperimentare, non credere. Nella via spirituale non c'è nulla da credere, c'è tutto da sperimentare. E dobbiamo poter mettere in dubbio tutte le cose che ci hanno detto, comprese quelle che sto dicendo io. Anzitutto, dobbiamo accettare l'idea di essere persone condizionate e quindi che gli altri hanno immesso dentro il nostro cervellino un sacco di idee che non sono vere, perché a loro volta gliel'hanno immesse i loro genitori e a loro i loro genitori. \n" +
            "\n" +
            "Una delle cose da sfatare è che siamo pieni di schemi e che la persona che ha fatto un cammino spirituale dev'essere serafica, non si lascia travolgere da nulla. Questo in parte è vero: non si lascia condizionare, il che in parte è vero, ma non reagisce all'esterno, il che non è vero. C'è quella storiella comica del guru che sta nell'ascensore col discepolo. Passa uno, entra nell'ascensore, gli sputa addosso e poi se ne va. Il guru non reagisce. Il discepolo dice: \"Maestro, perché non hai reagito?\". E lui risponde: \"Beh, perché non è un problema mio, è un problema dell'altro\". \n" +
            "\n" +
            "Sì, però lo sputo in faccia te lo sei preso tu. Quello si chiama anestesia. Già lo facciamo tanto, siamo tutti iniziati sotto questo profilo. Noi siamo tutti un po' anestetizzati. Invece dobbiamo risvegliarci. Diffidiamo dai guru, soprattutto dai guru che parlano come se sputassero sentenze, che non sono ladri, che non sono vivaci, che non sono spontanei. \n" +
            "\n" +
            "La persona che ha capito un po' più degli altri, diciamolo così, invece che guru ha la possibilità sia di insegnare qualcosa agli altri, ma senza giudicare gli altri. Non si sente superiore, perché più andiamo avanti nel cammino spirituale e meno ci sentiamo importanti. È strano a dirsi, questo ci rende felici. Perdiamo quel personaggio che in parte ci hanno appiccicato e in parte ci siamo appiccicati noi, nel senso: \"Come devo fare per piacere agli altri? Come devo fare per essere accettato dagli altri? Come devo essere? Come faccio a essere simpatica o simpatico?\". \n" +
            "\n" +
            "E allora ci costruiamo un personaggio. Cerchiamo di essere molto perbene, molto seguaci, oppure molto spiritosi, oppure molto intelligenti. Dipende dal carattere, però cerchiamo di essere non come noi siamo, cerchiamo di crearci una facciata, quella che gli antichi chiamavano la maschera. E con quella viviamo male, perché chiunque ci dice: \"Stai indossando una maschera\", ci sentiamo morire. Abbiamo paura di essere presi in giro, abbiamo una maschera da salvare. \n" +
            "\n" +
            "Ce ne accorgiamo da questo: non siamo realizzati, perché chiunque può ferirci attribuendoci qualcosa che non corrisponde al nostro personaggio. E allora, se ci dicono: \"Ma sei noiosa, sei ripetitiva, sei bugiarda, sei eccetera eccetera\", l'importante è quello che sentiamo dentro di noi. Quello che dice l'altro diventa importante solo per capire la ragione per cui l'altro sta parlando. \n" +
            "\n" +
            "E questo non è facile da fare, perché quando l'altro parla noi pensiamo sempre a cosa sta dicendo di noi, come ci reputa. Non perché lo sto dicendo. Se riusciamo a vedere l'altro oltre quello che sta facendo, siamo salvi. Riusciamo a capire perché ci aggredisce, se ci aggredisce; riusciamo a capire perché ci apprezza o ci adula; riusciamo a capire le motivazioni dell'altro. \n" +
            "\n" +
            "La via spirituale non è in cielo, la via spirituale è in terra. La via spirituale è molto fatta di terra, accetta tutto ciò che è terrestre ed è per questo che riesce a varcare i confini, riesce a vincere i condizionamenti, i pregiudizi, i giudizi. Il fatto di giudicare va un po' d'istinto, non di istinto distinto, perché noi non dobbiamo perdere il nostro criterio di giudizio. \n" +
            "\n" +
            "Se io vedo una donna vestita di rosso, dico che è vestita di rosso e posso dire che quel colore le sta bene o sta male, a nostro avviso. Non è un giudizio, non giudico la persona. Se dico che una persona è balbuziente, non devo ritenerlo per questo inferiore a un altro, però posso osservare che è balbuziente, che ha una difficoltà, ma questo non inficia sul valore. \n" +
            "\n" +
            "Se io dico che una persona ha fatto delle cose sconvenienti per la nostra società, per esempio, una donna che si prostituisce, molta gente la giudicherebbe. In realtà, noi stiamo usando un pregiudizio, perché chi si prostituisce non fa niente di male. Essere liberi da pregiudizi non è facile, ma è un buon inizio il provarci. \n" +
            "\n" +
            "Il mondo non è come lo vediamo. Il mondo, nella nostra mente, ha degli stretti confini. Aprire la mente permette di andare al di là di questi confini e di vedere un mondo che neanche ci sogniamo. Non perché noi vediamo il falso, la fisica è la fisica, ma perché non lo vediamo con lo stesso impatto emotivo con cui lo vediamo quando siamo liberi. \n" +
            "\n" +
            "Faccio un esempio: gli antichi guardavano un albero e dicevano che dentro quell'albero c'era una ninfa, una amadriade, una driade. Loro vedevano un'entità viva dentro l'albero, non vedevano la madrigal e non vedevano la ninfa, però vedevano la vita dell'albero. Sapevano che l'albero aveva una sua intelligenza, la sua vita. \n" +
            "\n" +
            "Beh, sono passati duemila anni e abbiamo detto: \"Howard, hanno anche le piante un'intelligenza, anche gli alberi, eccetera\". Perché prima avevamo un pregiudizio e lo abbiamo tutt'ora, ed è che noi umani siamo gli unici esseri intelligenti sulla terra. Gli altri sono simili, intelligenti, non sono intelligenti, perché l'uomo soffre. \n" +
            "\n" +
            "Perché l'uomo soffre di antropocentrismo, si sente al centro dell'universo, pensa che l'universo sia stato fatto per lui, che esista un dio che è stato fatto per lui, no, che sta facendo, che ha pensato solo a lui, che ha fatto l'universo per lui. Si sente al centro di tutto e questa è la mente condizionata. L'universo è molto più vasto di così, molto più allegro di così, molto meno punitivo di così, molto più fantastico di così.");



}
