package com.example.project_back.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    String uploadFile(MultipartFile file);

    void deleteFile(String filePath);

    void deleteFiles(List<String> filePaths);
}
