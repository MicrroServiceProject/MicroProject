package com.esprit.microservice.user.services;

import com.esprit.microservice.user.entities.GoogleUserInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private GoogleIdTokenVerifier verifier;
    public void someMethod() {
        System.out.println("Google Client ID: " + clientId);
        System.out.println("Google Client Secret: " + clientSecret);
    }
    @PostConstruct
    public void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
        logger.info("GoogleIdTokenVerifier initialized with clientId: {}", clientId);
    }

    public GoogleUserInfo verifyGoogleToken(String idToken) throws IOException {
        if (idToken == null || idToken.isEmpty()) {
            logger.error("Empty or null Google token provided");
            throw new IllegalArgumentException("Google token cannot be empty");
        }

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                logger.error("Invalid Google token: verification failed");
                throw new RuntimeException("Invalid Google token: verification failed");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            // Additional validation
            if (!payload.getAudience().equals(clientId)) {
                logger.error("Token audience doesn't match clientId");
                throw new RuntimeException("Invalid token audience");
            }

            return new GoogleUserInfo(
                    payload.getSubject(),
                    payload.getEmail(),
                    (String) payload.get("given_name"),
                    (String) payload.get("family_name")
            );

        } catch (IllegalArgumentException e) {
            logger.error("Invalid token format: {}", e.getMessage());
            throw new RuntimeException("Invalid token format", e);
        } catch (Exception e) {
            logger.error("Error verifying Google token: {}", e.getMessage());
            throw new RuntimeException("Error verifying Google token", e);
        }
    }
}