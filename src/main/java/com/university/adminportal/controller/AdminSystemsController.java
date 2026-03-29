package com.university.adminportal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin-systems")
public class AdminSystemsController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> body = new HashMap<>();
        body.put("category", "Administrative Systems");
        body.put("capabilities", List.of(
                "Track service performance and turnaround times",
                "Generate administrative reports and dashboards",
                "Support audits, inspections, and compliance reporting"
        ));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> payload) {
        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiBaseUrl + "/api/admin-systems/report", payload, Map.class);
        return ResponseEntity.ok(aiResponse);
    }
}

