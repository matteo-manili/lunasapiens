package com.lunasapiens.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoService {

    private final Map<String, byte[]> videoMap = new HashMap<>();
    private static final long MEMORY_THRESHOLD = 100 * 1024 * 1024; // Limite di memoria in byte (es. 100 MB)

    public void saveVideo(String videoName, byte[] videoBytes) {
        videoMap.put(videoName, videoBytes);
        checkMemoryUsage();
    }

    public byte[] getVideoByName(String videoName) {
        return videoMap.get(videoName);
    }

    public boolean videoExists(String videoName) {
        return videoMap.containsKey(videoName);
    }

    public void deleteVideo(String videoName) {
        videoMap.remove(videoName);
    }

    private void checkMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        // Se l'utilizzo della memoria supera la soglia, elimina i video per liberare spazio
        if (usedMemory > MEMORY_THRESHOLD) {
            videoMap.clear(); // Elimina tutti i video per liberare spazio
        }
    }
}

