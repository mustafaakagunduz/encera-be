package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.response.FileUploadResponse;
import com.pappgroup.pappapp.dto.response.MultipleFileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileUploadService {

    @Autowired
    private IStorageService storageService;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    public FileUploadResponse uploadSingleFile(MultipartFile file, String subDirectory) throws IOException {
        validateFile(file);

        String fileUrl = storageService.uploadFile(file, subDirectory);

        return new FileUploadResponse(
                fileUrl,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );
    }

    public MultipleFileUploadResponse uploadMultipleFiles(MultipartFile[] files, String subDirectory) {
        List<FileUploadResponse> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                validateFile(file);
                String fileUrl = storageService.uploadFile(file, subDirectory);
                uploadedFiles.add(new FileUploadResponse(
                        fileUrl,
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType()
                ));
            } catch (Exception e) {
                failedFiles.add(file.getOriginalFilename() + " - " + e.getMessage());
            }
        }

        return new MultipleFileUploadResponse(
                uploadedFiles,
                failedFiles,
                files.length,
                uploadedFiles.size(),
                failedFiles.size()
        );
    }

    public void deleteFile(String fileUrl) {
        storageService.deleteFile(fileUrl);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size: " + formatFileSize(maxFileSize));
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + allowedTypes);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
        return allowedTypesList.stream()
                .anyMatch(type -> type.trim().equalsIgnoreCase(contentType));
    }

    private String formatFileSize(long size) {
        if (size >= 1024 * 1024) {
            return (size / (1024 * 1024)) + " MB";
        } else if (size >= 1024) {
            return (size / 1024) + " KB";
        } else {
            return size + " bytes";
        }
    }
}