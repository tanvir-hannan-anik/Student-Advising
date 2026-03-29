package com.university.adminportal.service;

import com.university.adminportal.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserSessionRepository sessionRepository;

    public String getEmailFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7).trim();
        return sessionRepository.findByToken(token)
                .map(s -> s.getEmail())
                .orElse(null);
    }
}
