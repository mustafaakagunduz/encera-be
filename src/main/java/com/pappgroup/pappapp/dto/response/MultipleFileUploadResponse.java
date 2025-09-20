package com.pappgroup.pappapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleFileUploadResponse {
    private List<FileUploadResponse> uploadedFiles;
    private List<String> failedFiles;
    private int totalFiles;
    private int successCount;
    private int failureCount;
}