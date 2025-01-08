package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.service.FileService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static io.ndk.cordis_backend.constan.Constant.PHOTO_DIR;

@Component
public class FileServiceImpl implements FileService {

    private final String defaultFileName = "default.png";

    @Override
    public String saveFile(MultipartFile file) {
        try {
            Path folderPath = Paths.get(PHOTO_DIR);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return PHOTO_DIR+fileName;
        } catch (IOException e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_FETCH_FAILED);
        }
    }

    @Override
    public String updateFile(MultipartFile file, String existingFileName) {
        try {
            if (!defaultFileName.equals(existingFileName)) {
                deleteFile(existingFileName);
            }

            return saveFile(file);
        } catch (Exception e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_UPDATE_FAILED);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {

            Path file = Paths.get(PHOTO_DIR+filePath);
            String fileName = file.getFileName().toString();

            if (Files.exists(file) && !"default.jpg".equals(fileName)) {
                Files.delete(file);
            }

        } catch (IOException e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_NOT_FOUND);
        }
    }

    @Override
    public String getDefault() {
        return PHOTO_DIR + defaultFileName;
    }
}
