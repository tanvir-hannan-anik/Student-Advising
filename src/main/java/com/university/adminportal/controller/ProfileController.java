package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.User;
import com.university.adminportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile() {
        Optional<User> opt = userRepository.findByEmail(currentUser.getEmail());
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        User u = opt.get();
        Map<String, Object> body = new HashMap<>();
        body.put("name", u.getName());
        body.put("email", u.getEmail());
        body.put("batch", u.getBatch() != null ? u.getBatch() : "");
        body.put("studentId", u.getStudentId() != null ? u.getStudentId() : "");
        body.put("department", u.getDepartment() != null ? u.getDepartment() : "");
        body.put("profilePicture", u.getProfilePicture() != null ? u.getProfilePicture() : "");
        body.put("memberSince", u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate().toString() : "");
        return ResponseEntity.ok(body);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> payload) {
        Optional<User> opt = userRepository.findByEmail(currentUser.getEmail());
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        User u = opt.get();
        if (payload.containsKey("name") && !((String) payload.get("name")).isBlank())
            u.setName((String) payload.get("name"));
        if (payload.containsKey("batch"))   u.setBatch((String) payload.get("batch"));
        if (payload.containsKey("studentId")) u.setStudentId((String) payload.get("studentId"));
        if (payload.containsKey("department")) u.setDepartment((String) payload.get("department"));
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
    }

    @PostMapping("/picture")
    public ResponseEntity<Map<String, Object>> updatePicture(@RequestBody Map<String, Object> payload) {
        Optional<User> opt = userRepository.findByEmail(currentUser.getEmail());
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        String picture = (String) payload.get("picture");
        if (picture == null || picture.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "No picture data provided."));
        User u = opt.get();
        u.setProfilePicture(picture);
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Profile picture updated."));
    }
}
