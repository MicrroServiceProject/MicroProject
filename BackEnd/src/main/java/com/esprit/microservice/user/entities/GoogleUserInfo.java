package com.esprit.microservice.user.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleUserInfo {
    private String googleId;
    private String email;
    private String firstName;
    private String lastName;

    // Default constructor required by Spring
    public GoogleUserInfo() {
    }

    // Constructor for manual creation
    public GoogleUserInfo(String googleId, String email, String firstName, String lastName) {
        this.googleId = googleId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public String getGoogleId() {
        return googleId;
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}