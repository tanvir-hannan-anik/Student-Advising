package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.CommunicationRecord;
import com.university.adminportal.repository.CommunicationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @Autowired
    private CommunicationRecordRepository recordRepository;

    @Autowired
    private CurrentUser currentUser;

    @SuppressWarnings("unchecked")
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> aiResponse = (Map<String, Object>) restTemplate.postForObject(
                    aiBaseUrl + "/api/communication/query", payload, Map.class);
            return ResponseEntity.ok(aiResponse != null ? aiResponse : Map.of("response", "AI service unavailable."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("response", "AI service error: " + e.getMessage()));
        }
    }

    @GetMapping("/channels")
    public ResponseEntity<Map<String, Object>> getChannels() {
        Map<String, Object> body = new HashMap<>();
        body.put("category", "Internal Communication");
        body.put("channels", List.of(
                "Faculty announcements",
                "Student notices",
                "Administrative memos",
                "Meeting agendas and minutes"
        ));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> draftMessage(@RequestBody Map<String, Object> payload) {
        String audience = (String) payload.getOrDefault("audience", "students");
        String topic    = (String) payload.getOrDefault("topic", "general update");
        String type     = (String) payload.getOrDefault("type", "announcement");

        String draft = "Dear " + audience + ",\n\nThis is an automated draft regarding: "
                + topic + ". Please review and customize before sending.\n";

        CommunicationRecord record = new CommunicationRecord();
        record.setStudentId(currentUser.getEmail());
        record.setType(type);
        record.setSubject(topic);
        record.setContent(draft);
        record.setRecipient(audience);
        recordRepository.save(record);

        Map<String, Object> response = new HashMap<>();
        response.put("audience", audience);
        response.put("topic", topic);
        response.put("draft", draft);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CommunicationRecord>> getMyHistory() {
        return ResponseEntity.ok(recordRepository.findByStudentIdOrderByCreatedAtDesc(currentUser.getEmail()));
    }
}
