package com.sb1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;


    @Autowired
    private CloudWatchService cwService;

    public void uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String fileName = file.getOriginalFilename();

        try {
            s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileName).build(), RequestBody.fromBytes(file.getBytes()));

            cwService.logMessage("File uploaded successfully to S3: " + fileName);

        } catch (IOException e) {
            cwService.logMessage("IO error while uploading file: " + e.getMessage());
            throw new RuntimeException("Failed to upload file", e);

        } catch (S3Exception e) {
            cwService.logMessage("S3 error while uploading file: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("AWS S3 upload failed", e);
        }
    }


    public byte[] downloadFile(String key) {

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("File key is null or empty");
        }

        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(key).build());

            cwService.logMessage("File downloaded successfully from S3: " + key);
            return objectBytes.asByteArray();

        } catch (NoSuchKeyException e) {
            cwService.logMessage("File not found in S3: " + key);
            throw new RuntimeException("File not found", e);

        } catch (S3Exception e) {
            cwService.logMessage("S3 error while downloading file: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("AWS S3 download failed", e);
        }
    }


    public byte[] getImage(String fileName) {

        GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(fileName).build();
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
        try {
            return s3Object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading image from S3", e);
        }
    }
}