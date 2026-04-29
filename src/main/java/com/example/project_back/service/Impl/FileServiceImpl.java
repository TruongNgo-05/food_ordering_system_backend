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

    private final String UPLOAD_DIR = "uploads";

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            return "/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Upload file thất bại");
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) return;

            String fileName = filePath.replace("/uploads/", "");
            Path path = Paths.get(UPLOAD_DIR, fileName);

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