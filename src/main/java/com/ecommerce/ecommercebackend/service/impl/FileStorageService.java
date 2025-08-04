package com.ecommerce.ecommercebackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not create upload directory!", ex);
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    public String storeFile(MultipartFile file, String storeFolder, String type) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";

        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex >= 0) {
            fileExtension = originalFilename.substring(extensionIndex);
        }

        String filename = UUID.randomUUID().toString() + fileExtension;

        try {
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Invalid path sequence in file name " + originalFilename);
            }

            Path targetDir = this.fileStorageLocation.resolve(Paths.get(storeFolder, type)).normalize();
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return Paths.get(storeFolder, type, filename).toString().replace("\\", "/");
        } catch (IOException ex) {
            log.error("Could not store file {}. Please try again!", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }
}
