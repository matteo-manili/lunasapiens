package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class RateLimiter {

    private static final int MAX_MESSAGES_PER_MINUTE = 2; //15; // Limite di messaggi per minuto
    private static final long WINDOW_SIZE_MS = 60000; // 1 minuto in millisecondi

    @Autowired
    private CacheManager cacheManager;

    // userMessageCounts: Mappa che tiene traccia del numero di messaggi inviati da ogni utente.
    // Utilizza ConcurrentHashMap per gestire l'accesso concorrente.
    //La thread-safety di ConcurrentHashMap significa che le sue operazioni (come computeIfAbsent, put, get) sono sicure da usare in un contesto multi-thread.
    private final Map<String, MessageTracker> userMessageCounts = new ConcurrentHashMap<>();




    public static String numeroMessaggi_e_Minuti() {
        int windowSizeInMinutes = (int) (WINDOW_SIZE_MS / 60000); // Converti millisecondi in minuti
        return "Troppi messaggi! Per favore attendi. (Max "+ MAX_MESSAGES_PER_MINUTE +" messaggi in "+(windowSizeInMinutes+" "+(windowSizeInMinutes > 1 ? "minuti)" : "minuto)"));
    }



    public boolean allowMessage(String userId) {
        Cache cache = cacheManager.getCache(Constants.LIMITATORE_MESS_BOT_IA_CACHE);
        MessageTracker tracker = cache.get(userId, MessageTracker.class);
        if (tracker == null) {
            tracker = new MessageTracker();
            cache.put(userId, tracker);
        }
        return tracker.allowMessage();
    }







    // allowMessage(String userId): Metodo pubblico che verifica se l'utente ha raggiunto il limite di messaggi.
    // Se l'utente non esiste nella mappa, viene creato un nuovo MessageTracker.
    public boolean allowMessage_OLD(String userId) {

        // computeIfAbsent: Metodo della ConcurrentHashMap
        //Verifica se la chiave (userId) è presente nella mappa.
        // Se la chiave è presente, restituisce il valore associato.
        //  Se la chiave non è presente, esegue la funzione specificata per generare un nuovo valore, lo inserisce nella mappa e restituisce il nuovo valore.

        // Lambda k -> new MessageTracker():
        // k: È un parametro rappresentante la chiave (userId), che viene passato alla funzione lambda.
        // new MessageTracker(): È l'operazione che viene eseguita se la chiave non è presente nella mappa. Crea una nuova istanza di MessageTracker.
        MessageTracker tracker = userMessageCounts.computeIfAbsent(userId, k -> new MessageTracker());
        return tracker.allowMessage();
    }



    private class MessageTracker {

        // messageCount: Conta il numero di messaggi inviati dall'utente.
        private final AtomicInteger messageCount = new AtomicInteger(0);

        // startTime: Tempo di inizio della finestra temporale.
        private long startTime = System.currentTimeMillis();

        // allowMessage(): Metodo che verifica se l'utente può inviare un messaggio.
        // Se la finestra temporale è scaduta, il contatore viene reimpostato. Incrementa il contatore e ritorna true
        // se il numero di messaggi è inferiore o uguale al limite, altrimenti ritorna false.
        public synchronized boolean allowMessage() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > WINDOW_SIZE_MS) {
                startTime = currentTime;
                messageCount.set(0);
            }
            return messageCount.incrementAndGet() <= MAX_MESSAGES_PER_MINUTE;
        }
    }
}

