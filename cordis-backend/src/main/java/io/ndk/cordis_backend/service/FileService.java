package io.ndk.cordis_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file);
    String updateFile(MultipartFile file, String existingFileName);
    void deleteFile(String filePath);
    String getDefault();
}
