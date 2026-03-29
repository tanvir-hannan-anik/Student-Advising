package com.university.adminportal.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CurrentUser {
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
