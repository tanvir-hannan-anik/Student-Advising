package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.AdvisingSession;
import com.university.adminportal.repository.AdvisingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advising")
public class StudentAdvisingController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @Autowired
    private AdvisingSessionRepository sessionRepository;

    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        try {
            Map<String, Object> aiResponse = restTemplate.postForObject(
                    aiBaseUrl + "/api/advising/query", payload, Map.class);
            return ResponseEntity.ok(aiResponse != null ? aiResponse : Map.of("response", "AI service unavailable."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("response", "AI service error: " + e.getMessage()));
        }
    }

    @GetMapping("/faqs")
    public ResponseEntity<Map<String, Object>> getFaqs() {
        Map<String, Object> body = new HashMap<>();
        body.put("category", "Student Queries & Advising");
        body.put("faqs", List.of(
                "How do I select courses and check prerequisites?",
                "What are the rules for course add/drop and retake?",
                "How do I know if I'm at risk of academic probation?",
                "What are the guidelines for internship, capstone, and projects?"
        ));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/plan")
    public ResponseEntity<Map<String, Object>> getAcademicPlan(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiBaseUrl + "/api/advising/plan", payload, Map.class);

        AdvisingSession session = new AdvisingSession();
        session.setStudentId(currentUser.getEmail());
        session.setQueryType("academic-plan");
        session.setQueryContent(payload.toString());
        if (aiResponse != null) {
            session.setResponseContent(String.valueOf(aiResponse.getOrDefault("summary", aiResponse.toString())));
            session.setRiskLevel((String) aiResponse.getOrDefault("riskLevel", null));
        }
        sessionRepository.save(session);

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<AdvisingSession>> getMySessions() {
        return ResponseEntity.ok(sessionRepository.findByStudentIdOrderByCreatedAtDesc(currentUser.getEmail()));
    }
}
