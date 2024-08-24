package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Component
public class RateLimiterUser {

    public static final int MAX_MESSAGES_PER_DAY_UTENTE = 20; // 20 Limite di messaggi per giorno
    public static final int MAX_MESSAGES_PER_DAY_ANONYMOUS = 5; // 5 Limite di messaggi per giorno


    private static final long DAY_WINDOW_SIZE_MS = 86400000; // 1 giorno in millisecondi
    private static final long WINDOW_SIZE_MS = 60000; // 1 minuto in millisecondi
    private static final int MAX_MESSAGES_PER_MINUTE = 2; // 2 // Limite di messaggi per minuto





    @Autowired
    private CacheManager cacheManager;



    public static String numeroMessaggi_e_Minuti(int maxMessagePerDay) {
        int windowSizeInMinutes = (int) (WINDOW_SIZE_MS / 60000); // Converti millisecondi in minuti
        int windowSizeInDays = (int) (DAY_WINDOW_SIZE_MS / 86400000); // Converti millisecondi in giorni
        return "Troppi messaggi! (Max " + MAX_MESSAGES_PER_MINUTE + (MAX_MESSAGES_PER_MINUTE > 1 ? " messaggi" : " messaggio") + " in " + (windowSizeInMinutes > 1 ? windowSizeInMinutes + " minuti" : windowSizeInMinutes + " minuto") +
                ", " + maxMessagePerDay + " messaggi al giorno).";
    }



    public boolean allowMessage(String userId, int maxMessagePerDay) {
        Cache cache = cacheManager.getCache(Constants.LIMITATORE_MESS_BOT_IA_USER_CACHE);
        MessageTracker tracker = cache.get(userId, MessageTracker.class);
        if (tracker == null) {
            tracker = new MessageTracker();
            cache.put(userId, tracker);
        }
        return tracker.allowMessage(maxMessagePerDay);
    }

    private class MessageTracker {
        private final AtomicInteger messageCountPerMinute = new AtomicInteger(0);
        private final AtomicInteger messageCountPerDay = new AtomicInteger(0);

        private long startMinuteTime = System.currentTimeMillis();
        private long startDayTime = System.currentTimeMillis();

        public synchronized boolean allowMessage( int maxMessagePerDay ) {
            long currentTime = System.currentTimeMillis();

            // Controllo del limite per minuto
            if (currentTime - startMinuteTime > WINDOW_SIZE_MS) {
                startMinuteTime = currentTime;
                messageCountPerMinute.set(0);
            }

            // Controllo del limite giornaliero
            if (currentTime - startDayTime > DAY_WINDOW_SIZE_MS) {
                startDayTime = currentTime;
                messageCountPerDay.set(0);
            }

            if (messageCountPerMinute.incrementAndGet() <= MAX_MESSAGES_PER_MINUTE
                    && messageCountPerDay.incrementAndGet() <= maxMessagePerDay) {
                return true;
            } else {
                // Se uno dei limiti è superato, diminuiamo i contatori rispettivi
                messageCountPerMinute.decrementAndGet();
                messageCountPerDay.decrementAndGet();
                return false;
            }
        }
    }



}

