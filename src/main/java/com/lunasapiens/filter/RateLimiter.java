package com.lunasapiens.filter;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    private static final int MAX_MESSAGES_PER_MINUTE = 15; // Limite di messaggi per minuto
    private static final long WINDOW_SIZE_MS = 60000; // 1 minuto in millisecondi

    private final Map<String, MessageTracker> userMessageCounts = new ConcurrentHashMap<>();

    public boolean allowMessage(String userId) {
        MessageTracker tracker = userMessageCounts.computeIfAbsent(userId, k -> new MessageTracker());
        return tracker.allowMessage();
    }

    private class MessageTracker {
        private final AtomicInteger messageCount = new AtomicInteger(0);
        private long startTime = System.currentTimeMillis();

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

