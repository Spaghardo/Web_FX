package com.example.hello.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/products");
    private final Path identityUploadDir = Paths.get("src/main/resources/static/uploads/identities");

    public FileStorageService() {
        try {
            Files.createDirectories(uploadDir);
            Files.createDirectories(identityUploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Get original filename and extension
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Validate file type
            if (!isValidImageFile(extension)) {
                throw new RuntimeException("Only image files are allowed (JPG, JPEG, PNG, GIF, WebP)");
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

            // Copy file to upload directory
            Path destinationFile = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Return the URL path (not the file system path)
            return "/uploads/products/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }




    private boolean isValidImageFile(String extension) {
        String lowerExt = extension.toLowerCase();
        return lowerExt.equals(".jpg") || lowerExt.equals(".jpeg") || 
               lowerExt.equals(".png") || lowerExt.equals(".gif") || 
               lowerExt.equals(".webp");
    }
}
