package com.university.adminportal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @SuppressWarnings("unchecked")
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> aiResponse = (Map<String, Object>) restTemplate.postForObject(
                    aiBaseUrl + "/api/email/send", payload, Map.class);
            return ResponseEntity.ok(aiResponse != null ? aiResponse : Map.of("response", "AI service unavailable."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("response", "Email service error: " + e.getMessage()));
        }
    }
}
