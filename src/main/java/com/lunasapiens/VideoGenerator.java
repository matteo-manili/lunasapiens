package com.lunasapiens;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avutil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.bytedeco.ffmpeg.global.avcodec;

@Component
public class VideoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(VideoGenerator.class);
    private ResourceLoader resourceLoader;

    public VideoGenerator(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    private static final String pathStatic = "src/main/resources/static/";
    private static final String audioFilePath = pathStatic + "oroscopo_giornaliero/audio/music_9.mp3";
    public static final String pathOroscopoGiornalieroVideo = pathStatic + "oroscopo_giornaliero/video/";

    public VideoGenerator() {

    }


    public static String formatoVideo(){
        return ".mp4";
    }


    public static byte[] createVideoFromImages(String inputImagePath, String nomeFileVideo) {

        final int width = 700; final int height = 400; final int durataSecondiImmagine = 7;

        try {
            // Calcola la durata totale del video in base al numero di immagini
            File[] imageFiles = new File(inputImagePath).listFiles(File::isFile);
            int numImages = imageFiles != null ? imageFiles.length : 0;
            int totalDurationSeconds = numImages * durataSecondiImmagine; // Ogni immagine dura 5 secondi
            double frameRate = (double) numImages / totalDurationSeconds;

            Util.createDirectory( pathOroscopoGiornalieroVideo );

            // Inizializza il recorder per il video
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(pathOroscopoGiornalieroVideo + nomeFileVideo + formatoVideo(), width, height);
            recorder.setVideoCodec( avcodec.AV_CODEC_ID_H264 ); //avcodec.AV_CODEC_ID_MPEG4 // avcodec.AV_CODEC_ID_H264 // Imposta il codec video su MPEG4
            //recorder.setPixelFormat( avutil.AV_PIX_FMT_YUV420P10 ); //
            recorder.setFrameRate(frameRate); // Imposta il frame rate del video
            recorder.setVideoBitrate(20000); // Imposta il bitrate video a 2 Mbps


            // Inizializza il grabber per l'audio
            FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioFilePath);
            audioGrabber.start();

            // Aggiungi la traccia audio al recorder
            recorder.setAudioChannels(audioGrabber.getAudioChannels());
            recorder.setAudioCodec( avcodec.AV_CODEC_ID_MP3 ); // Imposta il codec audio
            recorder.setSampleRate(audioGrabber.getSampleRate());
            recorder.start();

            // Aggiungi le immagini al video
            for (File f : imageFiles) {
                if (f.isFile()) {
                    BufferedImage image = ImageIO.read(f);
                    BufferedImage convertedImage = convertImageToType(image, BufferedImage.TYPE_3BYTE_BGR);
                    Frame frame = bufferedImageToFrame(convertedImage);
                    // Registra il frame una volta per ogni immagine
                    recorder.record(frame);
                }
            }

            // Registra la traccia audio una volta

            Frame audioFrame;
            while ((audioFrame = audioGrabber.grabFrame()) != null) {
                recorder.record(audioFrame);
            }


            double secondsDurationAudio = getSecondsDurationAudio();
            logger.info( "durata traccia audio: "+secondsDurationAudio );
            for (int i = 0; i < (durataSecondiImmagine * numImages) / secondsDurationAudio; i++) {
                // Registra nuovamente la stessa traccia audio alla fine
                audioGrabber.setTimestamp(0); // Riporta il grabber audio all'inizio del file
                logger.info("registro una volta in più!");
                while ((audioFrame = audioGrabber.grabFrame()) != null) {
                    recorder.record(audioFrame);
                }
            }


            // Ferma e rilascia il recorder e il grabber audio
            recorder.stop();
            recorder.release();
            audioGrabber.stop();
            audioGrabber.release();

            byte[] videoBytes = Files.readAllBytes(Paths.get(pathOroscopoGiornalieroVideo, nomeFileVideo + formatoVideo()));





            // TODO questo non serve più farlo forse perché salvo il video nella classe VideoService
            // Copia il video nella cartella di destinazione
            /*
            Path destinationPath = Paths.get(pathOroscopoGiornalieroVideo, nomeFileVideo + formatoVideo());
            Files.createDirectories(destinationPath.getParent());
            Files.write(destinationPath, videoBytes);
            logger.info("HO salvato il video nella cartella: "+ destinationPath.toString());
            */


            return videoBytes;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    private static BufferedImage convertImageToType(BufferedImage originalImage, int targetType) {
        BufferedImage convertedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), targetType);
        convertedImage.getGraphics().drawImage(originalImage, 0, 0, null);
        return convertedImage;
    }


    private static Frame bufferedImageToFrame(BufferedImage bi) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getFrame(bi);
    }


    private static double getSecondsDurationAudio() {
        try {
            // Inizializza il grabber per l'audio
            FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioFilePath);
            audioGrabber.start();
            // Ottieni la durata totale della traccia audio in secondi
            double audioDurationSeconds = (double) audioGrabber.getLengthInTime() / 1000000.0;
            // Ferma il grabber audio
            audioGrabber.stop();
            audioGrabber.release();
            return audioDurationSeconds;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



}
