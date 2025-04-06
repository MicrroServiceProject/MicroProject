package esprit.microproject.Controllers;

import esprit.microproject.Entities.User;
import esprit.microproject.Services.UserService;
import esprit.microproject.dto.UserCreationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // For cleaner error handling

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/users") // Base path for user-related operations
@CrossOrigin(origins = "http://localhost:4200") // If needed for frontend
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreationRequest request) { // Use RequestBody
        // Optional: Add @Valid annotation to request if using validation constraints in DTO
        logger.info("Received request to create user: {}", request.getUsername());

        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank()) {
            logger.warn("User creation failed: Username or email is blank.");
            // Consider using @Valid for better handling
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and email cannot be blank");
        }

        // Basic email format check (more robust validation is possible)
        if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
            logger.warn("User creation failed for {}: Invalid email format.", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }


        try {
            User createdUser = userService.createUser(request);
            logger.info("User created successfully: {} (ID: {})", createdUser.getUsername(), createdUser.getId());

            // Return 201 Created status with the created user object and Location header
            URI location = new URI("/api/users/" + createdUser.getId()); // Adjust path if needed
            return ResponseEntity.created(location).body(createdUser);

        } catch (IllegalArgumentException e) { // Catch specific exceptions from service
            logger.warn("User creation failed for {}: {}", request.getUsername(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()); // 409 Conflict
        } catch (URISyntaxException e) {
            logger.error("Error creating location URI for new user {}", request.getUsername(), e);
            // Should not happen with static path, but handle anyway
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error creating user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    // Add other endpoints later (GET /api/users/{id}, GET /api/users, etc.)
}