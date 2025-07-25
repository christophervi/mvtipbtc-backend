package edu.gatech.cc.scp.mvtipbtc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class S3Service {
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    private S3Client s3Client;
    
    @Autowired
    private S3Presigner s3Presigner;
    
    public S3Service() {
        try {
            this.s3Client = S3Client.builder()
                    .region(Region.of("us-west-2"))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            // S3 client will be null if AWS credentials are not configured
            this.s3Client = null;
        }
    }
    
    public String uploadReport(byte[] reportData, String fileName) {
        if (s3Client == null) {
            // Return mock S3 key if AWS is not configured
            return "reports/" + fileName;
        }
        
        try {
            String key = "reports/" + fileName;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("application/pdf")
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(reportData));
            
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload report to S3", e);
        }
    }
    
    public String getReportUrl(String s3Key) {
        if (s3Client == null) {
            // Return mock URL if AWS is not configured
            return "https://mvtipbtc-reports.s3.amazonaws.com/" + s3Key;
        }
        
        try {
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            return s3Client.utilities().getUrl(getUrlRequest).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report URL", e);
        }
    }
    
    public boolean deleteReport(String s3Key) {
        if (s3Client == null) {
            return true; // Mock success if AWS is not configured
        }
        
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(s3Key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String generatePresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // The URL will be valid for 10 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}

