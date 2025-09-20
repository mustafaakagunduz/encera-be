package com.pappgroup.pappapp.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IStorageService {
    String uploadFile(MultipartFile file, String subDirectory) throws IOException;
    void deleteFile(String fileUrl);
}