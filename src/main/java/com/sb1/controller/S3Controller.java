package com.sb1.controller;

import com.sb1.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(file);
        return ResponseEntity.ok("File Uploaded Successfully");
    }

    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] data = s3Service.downloadFile(fileName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName).body(data);
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {

        byte[] imageBytes = s3Service.getImage(fileName);

        MediaType mediaType;

        if (fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.IMAGE_JPEG;
        }
        return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
    }

}
