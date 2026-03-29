package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.ScheduleReminder;
import com.university.adminportal.repository.ScheduleReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.base-url:http://localhost:5000}")
    private String aiBaseUrl;

    @Autowired
    private ScheduleReminderRepository reminderRepository;

    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        try {
            Map<String, Object> aiResponse = restTemplate.postForObject(
                    aiBaseUrl + "/api/scheduling/query", payload, Map.class);
            return ResponseEntity.ok(aiResponse != null ? aiResponse : Map.of("response", "AI service unavailable."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("response", "AI service error: " + e.getMessage()));
        }
    }

    @GetMapping("/reminders")
    public ResponseEntity<Map<String, Object>> getReminders() {
        List<ScheduleReminder> saved = reminderRepository.findByStudentIdOrderByDueDateAsc(currentUser.getEmail());
        Map<String, Object> body = new HashMap<>();
        body.put("category", "Scheduling & Notices");
        body.put("upcoming", List.of(
                "Registration deadline: " + LocalDate.now().plusDays(7),
                "Midterm exams start: " + LocalDate.now().plusDays(30),
                "Project submission deadline: " + LocalDate.now().plusDays(45)
        ));
        body.put("savedReminders", saved);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/reminders")
    public ResponseEntity<ScheduleReminder> createReminder(@RequestBody Map<String, Object> payload) {
        ScheduleReminder r = new ScheduleReminder();
        r.setStudentId(currentUser.getEmail());
        r.setTitle((String) payload.getOrDefault("title", "Reminder"));
        r.setDescription((String) payload.getOrDefault("description", ""));
        r.setReminderType((String) payload.getOrDefault("reminderType", "custom"));
        String dueDateStr = (String) payload.getOrDefault("dueDate", null);
        if (dueDateStr != null && !dueDateStr.isBlank()) r.setDueDate(LocalDate.parse(dueDateStr));
        return ResponseEntity.ok(reminderRepository.save(r));
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Long id) {
        if (!reminderRepository.existsById(id)) return ResponseEntity.notFound().build();
        reminderRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Reminder deleted"));
    }

    @PostMapping("/suggest")
    public ResponseEntity<Map<String, Object>> suggestSchedule(@RequestBody Map<String, Object> payload) {
        payload.put("studentId", currentUser.getEmail());
        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiBaseUrl + "/api/scheduling/suggest", payload, Map.class);
        return ResponseEntity.ok(aiResponse);
    }
}
