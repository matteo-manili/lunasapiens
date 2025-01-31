package com.lunasapiens.service;

import com.lunasapiens.config.S3ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.springframework.core.io.InputStreamResource;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;


    @Autowired
    public S3Service(S3ClientConfig s3ClientConfig) {

        this.bucketName = s3ClientConfig.getBucketName();

        AwsCredentials credentials = AwsBasicCredentials.create(
                s3ClientConfig.getAccessKey(), s3ClientConfig.getSecretKey());

        // Creazione del client S3 con credenziali statiche
        this.s3Client = S3Client.builder()
                .region(Region.of(s3ClientConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // Creazione del presigner con regione e credenziali.
        // Cioè la creazione di un url che ha una validità temporanea definita e che è usato per scaricare un'immagine (o un file)
        // del bucket dall'esterno senza autenticazione
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(s3ClientConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    // Carica un file su S3
    public void uploadFile(String fileName, InputStream inputStream) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
        } catch (S3Exception e) {
            throw new IOException("Errore durante il caricamento del file: " + e.awsErrorDetails().errorMessage(), e);
        }
    }


    public FileWithMetadata getImageFromS3(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3ObjectInputStream = s3Client.getObject(getObjectRequest);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = s3ObjectInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            // Ottieni il Content-Type dai metadati della risposta
            String contentType = s3ObjectInputStream.response().contentType();
            return new FileWithMetadata(byteArrayOutputStream.toByteArray(), contentType);
        } catch (S3Exception e) {
            return null;
        }
    }





    // Scarica un file da S3
    public InputStreamResource downloadFile(String fileName) throws IOException {
        // Creazione della richiesta per il recupero del file
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        try {
            // Ottenere il file come flusso di input
            ResponseInputStream<GetObjectResponse> s3ObjectInputStream = s3Client.getObject(getObjectRequest);

            // Restituire il flusso di input come InputStreamResource
            return new InputStreamResource(s3ObjectInputStream);
        } catch (S3Exception e) {
            throw new IOException("Errore durante il download del file: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    // Elimina un file da S3
    public void deleteFile(String fileName) throws IOException {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new IOException("Errore durante l'eliminazione del file: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    // Genera un URL presigned per il download di un file
    public URL generatePresignedUrl(String fileName, Duration duration) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            // Creazione della richiesta per ottenere l'URL presigned
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    presignRequest -> presignRequest.getObjectRequest(getObjectRequest)
                            .signatureDuration(duration)
            );

            // Restituisce l'URL pre-firmato
            return presignedRequest.url();
        } catch (Exception e) {
            throw new IOException("Errore durante la generazione dell'URL presigned: " + e.getMessage(), e);
        }
    }
}
