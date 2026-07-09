package com.example.project_back.service.Impl;

import com.example.project_back.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final String UPLOAD_DIR = "uploads";

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path folderPath = Paths.get(UPLOAD_DIR, folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(fileName);

            Files.write(filePath, file.getBytes());

            return "/uploads/" + folder + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Upload file thất bại", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) return;

            String relativePath = filePath.replaceFirst("^/uploads/", "");
            Path path = Paths.get(UPLOAD_DIR, relativePath);

            Files.deleteIfExists(path);

        } catch (Exception e) {
            System.out.println("Không thể xóa file: " + filePath);
        }
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        if (filePaths == null) return;

        for (String path : filePaths) {
            deleteFile(path);
        }
    }
}