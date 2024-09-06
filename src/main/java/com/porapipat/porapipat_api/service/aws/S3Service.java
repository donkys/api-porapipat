package com.porapipat.porapipat_api.service.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;


@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket.markdown}")
    private String markdownBucket;

    @Value("${aws.s3.bucket.image}")
    private String imageBucket;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String uploadMarkdownFile(String fileName, String content) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(markdownBucket)
                .key(fileName)
                .build();

        s3Client.putObject(putOb, RequestBody.fromString(content));

        return fileName;
    }

    public String uploadImage(String fileName, byte[] imageBytes) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(imageBucket)
                .key(fileName)
                .build();

        s3Client.putObject(putOb, RequestBody.fromBytes(imageBytes));

        return fileName;
    }

    public String generatePresignedUrl(String bucket, String fileName) {
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 minutes
                .getObjectRequest(b -> b.bucket(bucket).key(fileName))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    public String getMarkdownPresignedUrl(String fileName) {
        return generatePresignedUrl(markdownBucket, fileName);
    }

    public String getImagePresignedUrl(String fileName) {
        return generatePresignedUrl(imageBucket, fileName);
    }
}

