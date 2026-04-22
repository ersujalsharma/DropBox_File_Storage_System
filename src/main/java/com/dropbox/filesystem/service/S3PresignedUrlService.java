package com.dropbox.filesystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final long uploadTtlSeconds;
    private final long downloadTtlSeconds;

    public S3PresignedUrlService(
            S3Presigner s3Presigner,
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.s3.upload-url-ttl-seconds:900}") long uploadTtlSeconds,
            @Value("${aws.s3.download-url-ttl-seconds:300}") long downloadTtlSeconds
    ) {
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        this.uploadTtlSeconds = uploadTtlSeconds;
        this.downloadTtlSeconds = downloadTtlSeconds;
    }

    public String generateUploadUrl(String objectKey) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(uploadTtlSeconds))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    public String generateDownloadUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(downloadTtlSeconds))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public long downloadExpirySeconds() {
        return downloadTtlSeconds;
    }
}
