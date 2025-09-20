package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.response.ErrorResponse;
import com.pappgroup.pappapp.dto.response.FileUploadResponse;
import com.pappgroup.pappapp.dto.response.MultipleFileUploadResponse;
import com.pappgroup.pappapp.dto.response.SuccessResponse;
import com.pappgroup.pappapp.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/single")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadSingleFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subDirectory", defaultValue = "general") String subDirectory) {
        try {
            FileUploadResponse response = fileUploadService.uploadSingleFile(file, subDirectory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("File upload failed", e.getMessage())
            );
        }
    }

    @PostMapping("/multiple")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "subDirectory", defaultValue = "general") String subDirectory) {
        try {
            MultipleFileUploadResponse response = fileUploadService.uploadMultipleFiles(files, subDirectory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Multiple file upload failed", e.getMessage())
            );
        }
    }

    @PostMapping("/property")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadPropertyImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("propertyId") Long propertyId) {
        try {
            String subDirectory = "property-" + propertyId;
            MultipleFileUploadResponse response = fileUploadService.uploadMultipleFiles(files, subDirectory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Property image upload failed", e.getMessage())
            );
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileUploadService.uploadSingleFile(file, "profiles");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Profile picture upload failed", e.getMessage())
            );
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            fileUploadService.deleteFile(fileUrl);
            return ResponseEntity.ok(new SuccessResponse("File deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("File deletion failed", e.getMessage())
            );
        }
    }
}