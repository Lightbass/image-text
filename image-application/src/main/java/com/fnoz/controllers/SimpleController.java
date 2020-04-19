package com.fnoz.controllers;

import com.fnoz.dto.DataDTO;
import com.fnoz.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Controller
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    private FileService fileService;

    @Autowired
    public SimpleController(FileService fileService)  {
        this.fileService = fileService;
    }

    @GetMapping("/allo")
    public ResponseEntity homePage(Model model) {
        model.addAttribute("appName", appName);
        System.out.println("{ \"allo\": \"ALLO\"");
        return ResponseEntity.ok().body("{ \"appName\": \"" + appName + "\"}");
    }

    @PostMapping(value = "/images", consumes = { "multipart/form-data" })
    public CompletableFuture<ResponseEntity> saveImage(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("text") String text,
                                                       @RequestParam("size") Integer size,
                                                       @RequestParam("x") Integer x,
                                                       @RequestParam("x") Integer y) {
        return CompletableFuture.runAsync(() -> fileService.saveImageText(file, text, x, y, size))
                .thenApply(ResponseEntity::ok);
//        return CompletableFuture.runAsync(() -> fileService.saveImage(file))
//                .thenApply(ResponseEntity::ok);
    }

    @PostMapping(value = "/generate", consumes={"application/json"})
    public CompletableFuture<ResponseEntity> saveImage(@RequestBody DataDTO data) {
        return CompletableFuture.runAsync(() -> fileService.saveImageText(null, data.getText(), 0, 0, 60))
                .thenApply(ResponseEntity::ok);
    }
}