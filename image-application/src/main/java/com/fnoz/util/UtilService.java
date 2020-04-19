package com.fnoz.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class UtilService {

    public static void checkFolders(Path rootLocation) {
        if (!Files.exists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String copyFileToPathAndGetName(MultipartFile file, Path path) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName == null ? null : fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
        String hashFileName = UUID.randomUUID().toString();
        try {
            Files.copy(file.getInputStream(), path.resolve(hashFileName + fileExtension),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(file.getName());
        }
        return hashFileName + fileExtension;
    }

    public static String getMimeType(File file) {
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mimeType;
    }

}
