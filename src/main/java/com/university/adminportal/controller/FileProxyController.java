package com.university.adminportal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class FileProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @SuppressWarnings("unchecked")
    @PostMapping("/api/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String category,
            @RequestParam(value = "student_id", required = false) String studentId) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override public String getFilename() { return file.getOriginalFilename(); }
        });
        if (category != null) body.add("category", category);
        if (studentId != null) body.add("student_id", studentId);

        try {
            Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(
                    aiBaseUrl + "/api/upload", new HttpEntity<>(body, headers), Map.class);
            return ResponseEntity.ok(response != null ? response : Map.of("error", "No response from AI service."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/api/pdf/analyse")
    public ResponseEntity<Map<String, Object>> analysePdf(
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "full") String mode) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override public String getFilename() { return file.getOriginalFilename(); }
        });
        body.add("mode", mode);

        try {
            Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(
                    aiBaseUrl + "/api/pdf/analyse", new HttpEntity<>(body, headers), Map.class);
            return ResponseEntity.ok(response != null ? response : Map.of("response", "No response from AI service."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("response", "PDF analysis failed: " + e.getMessage()));
        }
    }
}
