package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.GeneratedDocument;
import com.university.adminportal.repository.GeneratedDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documentation")
public class DocumentationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @Autowired
    private GeneratedDocumentRepository documentRepository;

    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        try {
            Map<String, Object> aiResponse = restTemplate.postForObject(
                    aiBaseUrl + "/api/documentation/query", payload, Map.class);
            return ResponseEntity.ok(aiResponse != null ? aiResponse : Map.of("response", "AI service unavailable."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("response", "AI service error: " + e.getMessage()));
        }
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getDocumentTypes() {
        Map<String, Object> body = new HashMap<>();
        body.put("category", "Academic Documentation");
        body.put("forms", List.of(
                "Course registration form",
                "Course withdrawal form",
                "Credit transfer / exemption request",
                "Internship approval form",
                "Capstone project approval form"
        ));
        body.put("certificates", List.of(
                "Bonafide certificate",
                "No Objection Certificate (NOC)",
                "Recommendation letter",
                "Custom academic letter"
        ));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateDocument(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiBaseUrl + "/api/documentation/generate", payload, Map.class);

        GeneratedDocument doc = new GeneratedDocument();
        doc.setStudentId(currentUser.getEmail());
        doc.setDocumentType((String) payload.getOrDefault("docType", (String) payload.getOrDefault("genType", "unknown")));
        doc.setSubject((String) payload.getOrDefault("purpose", (String) payload.getOrDefault("name", "")));
        if (aiResponse != null) {
            doc.setContent(String.valueOf(aiResponse.getOrDefault("document",
                    aiResponse.getOrDefault("response", aiResponse.toString()))));
        }
        documentRepository.save(doc);

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<List<GeneratedDocument>> getMyHistory() {
        return ResponseEntity.ok(documentRepository.findByStudentIdOrderByCreatedAtDesc(currentUser.getEmail()));
    }
}
