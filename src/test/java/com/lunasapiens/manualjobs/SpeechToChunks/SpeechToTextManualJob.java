package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;

import com.lunasapiens.entity.VideoChunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.PunteggiaturaIAService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.AudioTranscriptionService;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.repository.VideoChunksRepository;
import com.lunasapiens.service.TextEmbeddingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpeechToTextManualJob {

    @Autowired
    private AudioTranscriptionService audioTranscriptionService;

    @Autowired
    private PunteggiaturaIAService punteggiaturaIAService;

    @Autowired
    private TextEmbeddingService textEmbeddingService;

    @Autowired
    private VideoChunksRepository videoChunksRepository;

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;





    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_VIDEO() throws Exception {

        Long VIDEO_ID = 60L;  // ID del video

        System.out.println("########## VIDEO "+String.valueOf(VIDEO_ID)+" ##########");

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
        videoChunks = videoChunksRepository.save(videoChunks); // salva nel DB

        // 4️⃣ Dividi il testo in chunk
        List<String> chunks = dividiTestoInChunk(testoPunteggiato.toString());
        //List<String> chunks = dividiTestoInChunk(TESTO_PUNTEGGIATO_117.toString());

        // 5️⃣ Salva i chunk nel database calcolando embedding reale
        salvaChunkInDatabase(chunks, videoChunks);
    }




    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniSpeechToChunks_TESTO() throws Exception {
        List<VideoChunks> list = videoChunksRepository.findAll();
        for(VideoChunks videoChunks: list){
            // 4️⃣ Dividi il testo in chunk
            List<String> chunks = dividiTestoInChunk(videoChunks.getFullContent() );
            //List<String> chunks = dividiTestoInChunk(TESTO_PUNTEGGIATO_117.toString());
            // 5️⃣ Salva i chunk nel database calcolando embedding reale
            salvaChunkInDatabase(chunks, videoChunks);
        }
    }




    public void salvaChunkInDatabase(List<String> chunks, VideoChunks videoChunks){
        int chunkIndex = 1;
        for (String chunkContent : chunks) {
            try {
                // Calcola embedding reale tramite TextEmbeddingService
                Float[] embedding = textEmbeddingService.computeCleanEmbedding(chunkContent);

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



    public List<String> dividiTestoInChunk(String testo) throws  Exception {
        //testo = "Buonasera a tutti! Mi chiamo Gabriella Tupini. Sto facendo dei video per comunicare alle persone interessate";
        List<String> chunks = chunkText_con_Overlap(testo);
        int index = 1;
        for (String c : chunks) {
            System.out.println("Chunk " + index++ + ":\n" + c + "\n---");
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



    /**
     * Divide un testo in chunk da ~400 parole, rispettando frasi e overlap del 15%
     */
    public static List<String> chunkText_con_Overlap(String text) {
        int chunkSize = 400;
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


    private StringBuilder TESTO_PUNTEGGIATO_117 = new StringBuilder("Buongiorno a tutti. Quest'oggi io parlerei, visto che la volta scorsa ho parlato dell'essere figli, dell'essere genitori, che è cosa tutt'altro che facile. Essere genitori deve partire anzitutto dall'idea del perché essere genitori. Voglio dire, essere genitori può capitare: una coppia fa figli perché capita, perché sono sposati, perché vogliono figli, oppure vengono anche se non li vogliono, eccetera eccetera. Allora parliamo soprattutto dei genitori che hanno dei figli voluti. \n" +
            "\n" +
            "Anzitutto io preciserei che i figli non è tanto importante volerli prima quanto è importante volerli dopo, cioè quando sono nati. Perché si possono non volere figli prima per mille ragioni, però quando sono venuti al mondo ci si affeziona, no? Cioè si vogliono dopo. Quindi non aver desiderato figli prima non è un indice di poco amore da parte dei genitori, assolutamente. Perché spesso i genitori dicono ai figli: \"Tu sei stato un figlio desiderato\". Non vuol dire niente; l'importante è che sia stato amato quando è nato. È anche perché le ragioni per cui si fanno i figli sono molteplici in genere. \n" +
            "\n" +
            "I genitori fanno i figli perché i figli si devono fare, perché la famiglia non ha senso se non ci sono figli. Perché la gente dice alla coppia, soprattutto alla donna: \"A quando il figlio?\". E quella pensa: \"Ma perché non si fa gli affari suoi?\". E non ha tutti i torti. Non bisognerebbe mai fare queste domande perché sono indiscrete. Poi ci sono coppie che non vogliono figli e qualcuno osa dire che sono egoisti. A volte non si fanno i figli perché si è più consapevoli di quelli che li fanno, in quanto fare figli non è un segno di generosità, mai. Non è nemmeno un segno di egoismo, ma non è un segno di generosità. \n" +
            "\n" +
            "Non è un segno di generosità perché quando noi decidiamo di fare dei figli non sappiamo mai che vita avranno i nostri figli. Potrebbero avere una vita molto sgradevole, magari semplicemente perché hanno un incidente, perché finiscono paralizzati su una sedia a rotelle. Perché invece stanno bene, però viene una guerra, viene una malattia, una pandemia, eccetera eccetera. Non sappiamo mai come sarà la vita dei nostri figli, per cui ci affidiamo al destino. Naturalmente si può essere ottimisti, eh, e dire: \"Mah, io penso che andrà bene, voglio fare un figlio\". Niente di male in questo, però è anche logico che qualcuno invece ci pensi. \n" +
            "\n" +
            "Quindi il dire che chi non fa figli è egoista non ha senso, anche perché l'umanità non ha bisogno di figli; l'umanità soffre di troppi figli. Il nostro malessere non è questa la causa vera, perché è una conseguenza. Però economicamente sta male perché la popolazione aumenta a dismisura, cioè in un modo assolutamente incontrollato. E quando noi riusciamo, noi terrestri, a inventare dei sistemi per cui la terra renda molto più cibo, rende molte più piante e quindi più cibo, nel frattempo la popolazione è aumentata. E per esempio l'invenzione dei concimi chimici, che tanto ha aumentato la disponibilità del cibo, è diminuita in quanto è aumentata la popolazione. C'è stato annientato il progresso, ha avuto dal progresso della popolazione. \n" +
            "\n" +
            "La popolazione dovrebbe essere incentivata a non fare figli, almeno a non farne, non so, più di una testa, come in Cina, che tra l'altro sta tentando forse di rivedere le sue idee. Male. E quindi fare i genitori può essere un desiderio di un amore da dare: \"Vorrei una creatura a cui dare amore\". Ci possa, cioè questo dovrebbe essere il senso di voler essere genitori. Spesso però non è così. Molti dicono: \"Io voglio essere genitore per dare un senso alla mia vita\". Se la vostra vita ha un senso perché fate i figli, non va bene. Non è segno di buon senso. I figli non possono dare uno scopo nella vita. Qual è lo scopo della vita? Mettere al mondo altre creature che sovraffollino il pianeta e magari che soffrano? Questo è lo scopo? Perché avete così da fare? Altrimenti non sapete cosa fare. \n" +
            "\n" +
            "Se volete aiutare gli altri, fatelo con quelli che già stanno male, non ne create altri. Ripeto, si può invece desiderare di fare figli semplicemente perché si ha un affetto da dare, così come si prende un gatto o un cane perché si ha un affetto da dare. Quindi non mi si fraintenda, non giudico chi fa figli, ma non giudico chi non li fa. Spesso chi non li fa viene giudicato e spesso si giudicano i gay perché non possono fare figli. Cose allucinanti. A proposito di genitori, io conosco gay che sono stati cacciati di casa perché gay. Quelli non erano genitori, quelli non sapevano amare, non hanno mai amato né gay né gli omosessuali eterosessuali. I figli si amano comunque, comunque siano. I figli non possono essere figli depravati, perché altrimenti vuol dire che hanno avuto genitori depravati. Possono essere diversi da noi in un modo che a noi fa un po' paura, ma non possono essere creature distruttive, altrimenti vuol dire che noi abbiamo cercato di distruggerli. \n" +
            "\n" +
            "Un figlio poco amato sicuramente è una brava persona e se ha idee innovative non per questo non è una brava persona. Il guaio dei genitori è che noi trasferiamo ai nostri figli quello che i genitori hanno trasferito a noi. Per esempio, il bisogno di obbedire. Noi per obbedire ai nostri genitori, specie se non avevano buon senso, e in genere i genitori non li hanno, perché non ce l'ha la popolazione, e cercano di adattarsi agli ordini incongrui, agli ordini forse esagerati, agli schemi incongrui dei genitori. Allora i miei genitori pensavano questo, quindi andava bene. Questo non lo mettono in discussione e lo trasferiscono ai figli. \n" +
            "\n" +
            "Una delle cose classiche è: \"Cosa pensano gli altri se tu fai così?\". Perché i genitori li hanno trasferito l'idea che è importante quello che pensano gli altri e magari gli altri pensano in modo incongruo o folle. Pensate a tutto quello che nelle epoche hanno creduto gli uomini. Per esempio, le streghe potevano andare sul rogo, che era giusto torturare e ammazzare persone se non erano credenti come loro. E in questo la religione cattolica ha il vessillo. E noi ci aspettiamo che i nostri figli la pensino come noi. I nostri figli sono diversi da noi. Quando facciamo un figlio dobbiamo aspettarci di non conoscerlo e dobbiamo desiderare di conoscerlo. Se pensiamo che la penserà come noi, meglio che non li facciamo ai figli, perché potremmo restare molto delusi. \n" +
            "\n" +
            "Allora, quando facciamo un figlio, vogliamo farlo, cominciamo a pensare che questo figlio può essere un rivoluzionario, può avere idee rivoluzionarie in questo senso. Perché se va a fare rivoluzionario davvero, c'è qualcosa che non va. Può avere idee molto diverse dalle nostre, può essere gay, può non volere figli a sua volta, può desiderare di fare un lavoro non eccelso ma che gli piace, perché forse i lavori che gli proponiamo noi non gli piacciono. Può desiderare di non accoppiarsi, può desiderare di accoppiarsi senza sposarsi, può desiderare di non avere figli, può essere ateo o può essere religioso. Ovviamente noi non dovremmo cercare di influenzarli. \n" +
            "\n" +
            "A questo proposito, battezzare i figli non significa: \"Io lascio mio figlio padrone di scegliere quando è grande\", perché voi scegliete quando è piccolo. E se scegliete che venga battezzato, è perché gli trasmettete le vostre idee. Se voi foste effettivamente neutri, non parlereste di nessuna religione o parlereste di tutte le religioni e direste soprattutto ai vostri figli cosa ne pensate delle religioni. Perché sarebbe più onesto. Cioè, dire al figlio: \"Io sono religioso, però ti lascio libero\" è una buona idea. \"Io credo in questa religione, credo in questa religione perché a me piace e perché ci sono nato\". \n" +
            "\n" +
            "Perché probabilmente se fossi giapponese sarei scintoista, un'altra cosa. Se fossi cinese sarei buddista, oppure ateo, e oppure sarei islamico e crederei in Allah e Maometto, a seconda di dove sono nato. Perché quello mi hanno dato. È una cosa che non pensa mai nessuno, che noi crediamo all'idea che ci sono stati insegnati e noi facciamo la stessa cosa ai nostri figli senza mai chiederci perché. Perché il male delle persone è che non chiedono mai perché. Non vediamo la magia del mondo proprio perché lo riduciamo a religione. \n" +
            "\n" +
            "Allora, un certo Cassirer, grande filosofo, scrisse tre tomi paurosi per la mole, dove descriveva le varie fasi della religione umana. La prima fase era quella animista: gli uomini credevano a quella che noi oggi chiameremmo magia, e cioè credevano nel genio dell'acqua, in quello della terra, nel genius loci, negli amorini equivalenti, in quelli, nonché un pochino si porta appresso la religione romana, quella pagana, l'orribile religione pagana esecrata dalla chiesa. Tutti all'inferno. Quando vedete quegli erotici e gli amorini, quelle figure pompeiane, erano un retaggio di quello. Le ninfe, i satiri erano tutti esseri della natura. Quindi credevano nella personificazione della natura, e cioè che ogni componente della natura avesse una sua intelligenza e una sua personalità. \n" +
            "\n" +
            "Una cosa che oggi scandalizza, anche se molti cominciano a contemplare che le piante abbiano la loro intelligenza. Da parecchio che lo dicevano i greci, da Aristotele e anche prima, da Democrito, eccetera. No, Platone. Insomma, l'intelligenza della creatura, l'anima mundi, voleva dire che aveva un'anima al mondo, che era senza un'anima. Ma ci ha fatto apparire la religione cattolica, che ha distrutto tutto. Per cui noi possiamo anche avvelenare la natura, farne un deserto per i nostri interessi, perché tanto non ha un'anima, non sente, non capisce. Stupida. Noi siamo le uniche creature intelligenti, pensiamo noi. Però nostra madre, la natura, è stupida. C'è qualche incongruenza in questo. Se noi abbiamo un'intelligenza nel cosmo, c'è intelligenza. Se noi proviamo amore nel cosmo, c'è amore. Se noi proviamo odio nel cosmo, c'è odio. Sia chiaro questo. Non abbiamo inventato nulla. Tutto quello che è in noi proviene da altro. \n" +
            "\n" +
            "Noi possiamo trasformarci, capendo, provando consapevolezza, ma non possiamo cambiare il mondo. Avere figli è un grosso carico. Chi non la pensa così è perché non sente doveri nei confronti dei figli, verso i figli. Eh, scusate, abbiamo un dovere di assistenza, che spesso eseguiamo, non sempre. Però abbiamo un dovere di assistenza morale, che non sempre siamo all'altezza di dare. Noi per assicurarci diamo ai figli la stessa fede che noi vorremmo avere, ma che non abbiamo. Perché se noi l'abbiamo, non vogliamo costringere nessuno ad averla. \n" +
            "\n" +
            "Attenzione: la chiesa cattolica ha voluto fare sempre proseliti proprio perché non c'era fede. Sai, io credo a qualcosa, ma ho molti dubbi. Spero che gli altri ci credano. Ci sono un prepotente, faccio di tutto perché gli altri ci credano e sono un delinquente, obbligo gli altri a crederci, perché così, se ci credono tutti, ci credo anche io. Arriviamo all'assurdità: io non ho fede, vorrei credere, ma non ho fede. Benissimo, risponde la chiesa: \"Chiedi a Dio di darti la fede\". Ma come faccio a chiedere a Dio se io in Dio non ci credo? Siamo all'assurdo. \n" +
            "\n" +
            "Io capisco che la fede è una grande rassicurazione. Capisco pure che ci sono persone che hanno avuto contatti con un divino, per cui si sono sentiti accolti e rassicurati. Io non so che cosa abbiano contattato queste persone; sicuramente qualcosa hanno contattato. In qualche caso quello che avevano contattato era un aspetto positivo di un genitore non troppo buono. Questo l'ho appurato. In altri casi non so cosa abbiano contattato, per cui non voglio assolutamente bocciare tutto ciò che non conosco. Non escludo che alcune persone abbiano incontrato un abbraccio, un sentimento, un qualcosa che le ha risollevate. Naturalmente non posso dire da che parte provenisse; sicuramente sarà una cosa positiva, doveva essere positiva, almeno si spera. \n" +
            "\n" +
            "Comunque, quando l'uomo sia più mentalizzato, perché l'uomo sia mentalizzato ha smesso di vedere l'intelligenza della natura, l'intelligenza delle parti della natura, perché ha visto se stesso. Si è messo lui al centro del mondo. Ecco la mentalizzazione: io sono il più importante, conto solo io. Questo è, virgolette, il peccato dell'uomo: si è staccato dalla natura e si è sentito al di fuori della natura. E a quel punto si è ammalato di solitudine, perché non appartiene più a nulla e cerca disperatamente di appartenere a qualcuno. Cerca la famiglia, cerca il gruppo, cerca una appartenenza, perché non appartiene alla natura. \n" +
            "\n" +
            "Io ricordo quando ero bambina: guardavo la natura, ne capivo la bellezza, e dicevo: \"Perché io non sono te? Perché io sono al di fuori di te?\". Poi un bel giorno ho sentito che c'ero anch'io, una natura. E ricordo che mi svegliai una mattina e dissi: \"Sono importante quanto una pianta del bosco\". E ciò mi riempie di gioia, perché ero rientrata nel luogo da cui ero uscita, da cui siamo usciti, da cui la mente ci fa uscire. La mente condizionata, non quella ragionata, che comunque anche quella ragionante viene condizionata da una mente condizionata. \n" +
            "\n" +
            "Dunque, essere genitori vuol dire lasciare liberi i figli, non di comportarsi in modo asociale, questo è chiaro, ma di credere in ciò che vogliono. Non che voi non possiate parlare, ma parlate dicendo la verità. Perché quando udite un figlio: \"Eh, io sono sicuro che questo esiste\", non è vero. Non è vero che siete sicuri della vostra fede, perché siete in genere sicuri della vostra fede fin tanto non vi muore una persona cara. Ne ho vista troppa di gente dire: \"Non credo più in Dio perché mi è successo questo\". Allora questa figura divina serve a rassicurarvi. \n" +
            "\n" +
            "Io ricordo che andavo a scuola e c'era gente che andava a fare la comunione tutte le mattine per andare bene a scuola. Pensate un po': se le cose non gli vanno bene, allora Dio non c'è. Allora una disgrazia perché Dio c'è solo per loro. Se la disgrazia avviene, gli aspri va bene. Mi diceva una persona: \"È morto mio marito\". Io domando sempre: \"Non poteva succedere agli altri e invece che a me?\". Perché c'è l'idea che questo Dio qualcuno si deve prendere, che l'ho fatto per cattiveria. Fatto Dio. Ma se aveva bisogno di vittime, poteva prendere da un'altra parte. Questo è il nostro pensiero. \n" +
            "\n" +
            "I nostri figli potrebbero pensarla al contrario di quello che pensiamo noi, potrebbero essere molto diversi da noi e noi non dovremmo cercare di cambiarli. Allora, quando la mente condizionata è avanzata, per cui noi abbiamo, ci siamo isolati dalla natura, dai, è una cosa. Noi siamo un altro. E cioè noi siamo qui, il resto è un film, non ci siamo dentro. Gli uomini si sono fabbricati gli dèi. Il paganesimo, il cosiddetto politeismo, cioè tanti dèi. Questa è la seconda fase dell'umanità: l'umanità comincia a pensare che ci sono degli umani che non sono umani, ma l'aspetto umano, per cui sono umani che in qualche modo tengono al loro, degli animali se ne fregano, naturalmente, perché l'uomo è il più importante. \n" +
            "\n" +
            "È questa esplosione dell'io, l'idealizzazione, il condizionamento mentale: io sono una creatura superiore a tutte le creature. Allora gli uomini si sono creati degli dèi: il dio della guerra li aiutava a fare la guerra, il dio della medicina li aiutava a guarire o curare gli altri. E avevano dei romani, avevano dèi per ogni cosa, perfino per andare al bagno. Avevamo un'infinità di dèi, però non erano fanatici. Loro si rivolgevano agli dèi per quello che serviva e ci andavano al tempio e dicevano al sacerdote di fare un certo rituale. Il sacerdote veniva pagato, il rituale spesso includeva l'uccisione di un animale. Tanto li ammazziamo, c'è poco da scandalizzarsi. E in genere venivano mangiati poi. E lui aveva soddisfatto la cosa, lo faceva solo una volta l'anno o in determinate occasioni o in determinate feste. \n" +
            "\n" +
            "E aver assolto ai suoi compiti, a meno che lui o lei non chiedesse a questa divinità di farle un, non dico un miracolo, ma di farle ottenere una grazia. Ecco, in questo caso veniva il do ut des, e cioè: \"Tu mi fai vincere questa guerra, io ti costruisco un tempio. Tu mi fai ottenere questo affare, io ti costruisco un altare\", eccetera eccetera. E infatti nelle epigrafi romane spesso si trova: \"Il dio si è comportato bene con me, per cui gli ho fatto questo\". Quando mai lo diremo di Dio? I romani non sentivano di dover agli dèi cose che oltrepassavano un rito ben definito. Per il resto non ci pensavano, a meno che non venisse una pestilenza o qualcosa di terribile. Allora si dovevano aver trascurato qualcosa, qualcosa doveva essere andato a male, per cui la cosiddetta pax deorum era rotta. \n" +
            "\n" +
            "E allora lì bisognava intervenire, ma c'erano i sacerdoti, i quali facevano cerimonie particolari per ristabilire la pace degli dèi con gli uomini. Cioè, quei romani che erano gli dèi dei romani, ognuno aveva i suoi, con la differenza che i romani spesso accoglievano altre idee. Anzi, addirittura se li fregavano. A volte prendevano i simulacri e se li riportavano in patria per onorarli, perché se erano stati bravi a riparare quel popolo, sarebbero stati riparati anche i romani. E comunque non erano mai gelosi dei loro dèi, cioè chiunque poteva onorare i suoi. Quindi sia chiaro che il fatto che il cristianesimo sia stato perseguitato non è stato dovuto a un imperativo dei romani. Cioè, i romani non è che non volessero che non avesse le sue religioni; lo facevano con tutti. Tutti i popoli potevano conservare la loro. \n" +
            "\n" +
            "Utilizzo era il cristianesimo che non voleva le religioni dei romani e questo per loro era sovvertire lo stato. Cioè, non puoi cambiare me, tu puoi fare quello che vuoi, ma in aggiunta. Tant'è vero che poi stabilirono una cosa molto civile, cioè il famoso libello, che era una specie di tessera, dove se il cristiano faceva un'offerta, cioè offriva un sacrificio all'imperatore, perché l'imperatore era sempre divinizzato, lui era libero, non aveva responsabilità, poteva adorare quello che gli pareva. Perché l'imperatore, perché quello che interessava allora lo stato, cioè che lui non si ribellasse allo stato. Questo era perché di quello in cui credeva non gliene importava nulla. Non erano fanatici, ma ci furono tanti cristiani che si sentirono in dovere di non sacrificare agli dèi per morire martirizzati, perché Dio era contento, in quanto era un Dio buono. \n" +
            "\n" +
            "Come può essere un Dio buono uno che vuole la tua morte con dolore? Ma voi lo vorreste per i vostri figli? Se non lo volete per i vostri figli, la maggior parte non dovrebbe volerlo un Dio che è infinitamente superiore a noi. Ma allora la religione politeistica faceva sì che quando un dio non concedeva le cose, o cedeva troppo, o il suo fedele pensava che chiedesse troppo, cambiava dio. Andava ad adorarlo in un altro. E siccome ce ne erano tanti, poteva cambiare come voleva, il che era molto più umano. Fino a che non è arrivato il monoteismo, è stata la fase più terribile e incivile dell'umanità. Credere in un solo dio, in un dio che non coincide con la natura, che rimane vuota, svuotata, rimane una cosa. Così come gli animali diventano una cosa, hanno valore solo l'uomo. E Dio chiede una cattiveria inaudita. \n" +
            "\n" +
            "E quell'io è un genitore che può andare da Abramo e dire: \"Sgozza mio sacco per dimostrarmi che mi vuoi bene\". Noi una figura così diremo è pazzo e criminale, ma l'ha detto Dio al padre. Non si discute. Il paterfamilias può anche uccidere il figlio, non riconoscendo. Questa è un'aberrazione mentale. Ma queste cose le abbiamo superate, in parte, solo in parte, perché qualcuno ha fatto delle leggi buone. Ma dentro di noi c'è sempre il fatto che i figli devono obbedire. La preoccupazione dell'educazione dei figli è che i figli devono obbedire. Nonché i figli devono stare bene, devono comprendere, devono capire, devono saper trasmettere, devono sapersi integrare, devono comunicare. No, deve obbedire, perché si obbedisce. Non si droga, si obbedisce. Non fa del male agli altri, non ruba, non fa le rivoluzioni, eccetera eccetera. Che sia felice o meno non ha importanza, l'importante è che sia obbediente. Questo è quello che in genere i genitori chiedono. \n" +
            "\n" +
            "I figli hanno il pallino dell'obbedienza. Di capire cosa succede al figlio, in genere te ne frega. Quando il figlio torna a casa, immediatamente gli domandano: \"Com'è andata a scuola?\". E il figlio non può più, poveretto. Ci sono altri figli che dicono: \"Non mi chiedeva mai come era andata la scuola\". Quindi non è che chiedere com'è andata a scuola sia un male, come il farlo non sia un bene necessariamente. Se si domanda solo quello, non va bene. Se si capisce che il figlio non vuole parlarne, bisogna rispettarlo. Se volete essere rispettati dai figli, dovete rispettare i figli. Rispettare i figli vuol dire che i genitori non devono porre i figli di fronte alla loro nudità. Devono imparare ai figli a coprirsi. \n" +
            "\n" +
            "Poi, dopo, possiamo fare quello che vogliono a casa loro. Ti stanno la partner, va benissimo. Ma i genitori devono rispettare i figli per far capire che c'è una separazione dalla sessualità dei genitori a quella dei figli. Ci sono troppi casi di pedofilia al mondo, ce ne sono tantissimi di cui non si può parlare, non si può dire. Quindi i genitori devono imparare a rispettare i figli. Li devono insegnare ai figli a rispettarsi. Quindi loro non possono comparire nudi di fronte ai figli, perché se qualcuno compare nudo di fronte ai figli, quello è un male. E allora il bambino capirà che quella persona non è una brava persona. E quindi ci dev'essere una sacra lontananza fra un genitore e i figli dell'altro sesso. Il padre non può lavare la figlia, la madre non può lavare il figlio, anche se già po' diversa la cosa, perché la madre è quella che lo fa fin dall'inizio, il padre no. \n" +
            "\n" +
            "Imparare una distanza fra genitori e figli, distanza fisica, ma non distanza animica. Cioè, i genitori devono poter parlare ai figli di tutto. Se i figli vogliono sapere, i genitori devono rispondere. I genitori devono dire la verità ai figli. Certo che c'è modo e modo di dirgli la verità, ma glielo devono dire. Se i genitori pensano che i nonni non siano delle brave persone, non possono dire ai figli che i nonni sono delle brave persone, perché li tradiscono. Se i genitori pensano che questo mondo non è buono, non possono dire ai figli che questo mondo è buono, perché li tradiscono. I genitori devono dire sempre la verità ai figli e se li amano trovano il modo giusto per farlo. \n" +
            "\n" +
            "I figli sono un onere. Non si fanno figli per avere il bastone della vecchiaia, perché è mostruoso. Ci si può preoccupare della propria vecchiaia, nel senso: \"Chi baderà a me se non ho figli?\". In qualche modo accadrà. Se state bene con la vostra coscienza, non vi preoccupate. La coscienza non è l'aver fatto le cose buone, ma avere una consapevolezza. Ho conosciuto gente che non aveva figli, non aveva parenti e che veniva accudita in modo straordinario da persone appena conosciute, perché erano un tale polo di attrazione che tutti li cercavano. Eccome, potevano, li aiutava. Poi esistono le badanti, eccetera eccetera. Non si fanno i figli come aiuto per la vecchiaia, perché è come fare un figlio per avere un asino che ci porta appresso. È come allevare una bestia e già quello è poco bello, figuriamoci un figlio. \n" +
            "\n" +
            "I figli sono un onere. Quando fate un figlio, pensate che il figlio potrà restituirvi quello che voi gli avete dato, perché magari è diverso, se ne va, se ne va a vivere all'estero e vi molla lì. Certo, e dovete anche fargli un sorriso se lo fa, è chiaro questo. Quindi la mentalità sana di un genitore è che il figlio è un onere. Se vi va di fare figli, pur essendo un onere, va bene. Non fate i figli per voi, perché non è un modo di amarli. I figli vanno amati proprio perché se ne vanno, altrimenti non li abbiamo mai veramente partoriti. I figli vanno amati perché sono diversi da noi, perché la pensano diversamente da noi, perché sono altro da noi. E figli sono anche gli altri. Se non amate gli altri figli, non amate i vostri. Voi amerete i vostri figli tanto quanto amate gli altri figli, se li amate perché sono nati da voi. È una forma di egoismo che non c'entra niente. I figli si amano perché si allevano. Sono più importanti dei figli degli altri, certo, perché riconosciamo, perché li alleviamo, perché tripli diamo per loro, ma non perché sono nostri, non per sangue. E chi siamo noi? Un saluto a tutti. Arrivederci.");



}
