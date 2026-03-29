package com.university.adminportal.controller;

import com.university.adminportal.model.User;
import com.university.adminportal.model.UserSession;
import com.university.adminportal.repository.UserRepository;
import com.university.adminportal.repository.UserSessionRepository;
import com.university.adminportal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private AuthService authService;

    // ── Register ──────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> payload) {
        String email = ((String) payload.getOrDefault("email", "")).trim().toLowerCase();
        String password = (String) payload.getOrDefault("password", "");
        String name = ((String) payload.getOrDefault("name", "")).trim();

        // Validate university email (.edu, .edu.bd, .edu.my, .ac.uk, .ac.in, etc.)
        if (!isUniversityEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only university email addresses are allowed (e.g. .edu, .edu.bd, .ac.uk)."));
        }
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters."));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "An account with this email already exists."));
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name.isEmpty() ? email.split("@")[0] : name);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);

        String token = createSession(email);
        return ResponseEntity.ok(Map.of("token", token, "email", email, "name", user.getName()));
    }

    // ── Login ─────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        String email = ((String) payload.getOrDefault("email", "")).trim().toLowerCase();
        String password = (String) payload.getOrDefault("password", "");

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty() || !passwordEncoder.matches(password, opt.get().getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password."));
        }

        String token = createSession(email);
        return ResponseEntity.ok(Map.of("token", token, "email", email, "name", opt.get().getName()));
    }

    // ── Logout ────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            sessionRepository.deleteByToken(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    // ── Verify token / get current user ───────────────────
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authService.getEmailFromHeader(authHeader);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated."));
        }
        return userRepository.findByEmail(email)
                .map(u -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("email", u.getEmail());
                    body.put("name", u.getName());
                    return ResponseEntity.ok(body);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "User not found.")));
    }

    // ── Helpers ───────────────────────────────────────────
    private boolean isUniversityEmail(String email) {
        String domain = email.contains("@") ? email.split("@")[1] : "";
        return domain.contains(".edu") || domain.contains(".ac.");
    }

    private String createSession(String email) {
        String token = UUID.randomUUID().toString();
        UserSession session = new UserSession();
        session.setEmail(email);
        session.setToken(token);
        sessionRepository.save(session);
        return token;
    }
}
