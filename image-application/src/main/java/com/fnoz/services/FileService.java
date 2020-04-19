package com.fnoz.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void saveImage(MultipartFile file);
    void saveImageText(MultipartFile file, String text, Integer x, Integer y, Integer size);
}
