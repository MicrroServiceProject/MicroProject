package tn.esprit.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backend.entities.Post;
import tn.esprit.backend.repositories.PostRepository;
import tn.esprit.backend.repositories.UserRepository;
import tn.esprit.backend.services.EmailService;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class NotifyAdminController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotifyAdminController(PostRepository postRepository, UserRepository userRepository, EmailService emailService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/notify-admin")
    public ResponseEntity<Void> notifyAdmin(@RequestBody NotificationRequest request) {
        System.out.println("Received notification request: " + request.getPostId() + ", Message: " + request.getMessage());

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + request.getPostId()));

        List<tn.esprit.backend.entities.User> admins = userRepository.findAll().stream()
                .filter(tn.esprit.backend.entities.User::isAdmin)
                .collect(java.util.stream.Collectors.toList());

        for (tn.esprit.backend.entities.User admin : admins) {
            // Instead of splitting, pass the entire message as the comment content
            emailService.sendCommentNotification(
                    admin.getEmail(),
                    admin.getUsername(),
                    post.getTitle(),
                    request.getMessage(), // Use the full message
                    post.getLikes(),
                    post.getViews()
            );
        }

        return ResponseEntity.ok().build();
    }

    // Inner class to represent the request body
    public static class NotificationRequest {
        private String postId;
        private String message;

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}