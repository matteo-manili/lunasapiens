package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Component
public class RateLimiterUser {

    public static final int MAX_MESSAGES_PER_DAY_UTENTE = 15; // 15 Limite di messaggi per giorno
    public static final int MAX_MESSAGES_PER_DAY_ANONYMOUS = 5; // 5 Limite di messaggi per giorno

    private static final long DAY_WINDOW_SIZE_MS = 86400000; // 1 giorno in millisecondi



    @Autowired
    private CacheManager cacheManager;


    public int getRemainingMessages(String userId, int maxMessagePerDay) {
        Cache cache = cacheManager.getCache(Constants.LIMITATORE_MESS_BOT_IA_USER_CACHE);
        MessageTracker tracker = cache.get(userId, MessageTracker.class);
        if (tracker == null) {
            tracker = new MessageTracker();
            cache.put(userId, tracker);
        }
        return tracker.getRemainingMessages(maxMessagePerDay);
    }




    public static String numeroMessaggi_e_Minuti(int maxMessagePerDay) {
        return "Hai superato il limite di "+maxMessagePerDay + " messaggi al giorno.";
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

        private final AtomicInteger messageCountPerDay = new AtomicInteger(0);

        private long startDayTime = System.currentTimeMillis();

        public synchronized boolean allowMessage( int maxMessagePerDay ) {
            long currentTime = System.currentTimeMillis();


            // Controllo del limite giornaliero
            if (currentTime - startDayTime > DAY_WINDOW_SIZE_MS) {
                startDayTime = currentTime;
                messageCountPerDay.set(0);
            }

            if (messageCountPerDay.incrementAndGet() <= maxMessagePerDay) {
                return true;
            } else {
                // Se uno dei limiti è superato, diminuiamo il contatore
                messageCountPerDay.decrementAndGet();
                return false;
            }
        }


        public synchronized int getRemainingMessages(int maxMessagePerDay) {
            long currentTime = System.currentTimeMillis();
            // Controllo del limite giornaliero
            if (currentTime - startDayTime > DAY_WINDOW_SIZE_MS) {
                startDayTime = currentTime;
                messageCountPerDay.set(0);
            }
            return maxMessagePerDay - messageCountPerDay.get() - 1;
        }

    }



}

