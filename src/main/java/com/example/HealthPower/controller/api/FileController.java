package com.example.HealthPower.controller.api;

import com.example.HealthPower.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class FileController {

    private final S3Service s3Service;

    @GetMapping("/presign")
    public Map<String, String> getPresignedUrl(@RequestParam String folder,
                               @RequestParam String filename,
                               @RequestParam String contentType) {

        URL presignedUrl = s3Service.generatePresignedUrl(folder, filename, contentType);
        String staticUrl = s3Service.generateStaticUrl(folder, filename);

        Map<String, String> result = new HashMap<>();
        result.put("presignedUrl", presignedUrl.toString());
        result.put("staticUrl", staticUrl);

        return result;

    }
}
