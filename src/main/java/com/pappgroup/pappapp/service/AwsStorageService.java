package com.pappgroup.pappapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.core.sync.RequestBody;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@Profile("prod")
public class AwsStorageService implements IStorageService {

    @Value("${aws.s3.endpoint:}")
    private String endpoint;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    private S3Client s3Client;

    @PostConstruct
    public void initializeS3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region));

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        this.s3Client = builder.build();
    }

    public String uploadFile(MultipartFile file, String subDirectory) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        String key = uploadDirectory + "/" + subDirectory + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                file.getInputStream(), file.getSize()));

        return getPublicUrl(key);
    }

    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String getPublicUrl(String key) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    public void makeFilePublic(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            PutObjectAclRequest putObjectAclRequest = PutObjectAclRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObjectAcl(putObjectAclRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to make file public: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5);
    }
}