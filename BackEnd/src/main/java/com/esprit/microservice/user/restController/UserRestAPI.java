package com.esprit.microservice.user.restController;

import com.esprit.microservice.user.security.JwtUtil;
import com.esprit.microservice.user.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.esprit.microservice.user.entities.User;
import com.esprit.microservice.user.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")

@RestController
@RequestMapping("/api/users")
public class UserRestAPI {
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        System.out.println("CORS Headers:");
        System.out.println("Origin: " + request.getHeader("Origin"));
        System.out.println("Access-Control-Request-Method: " + request.getHeader("Access-Control-Request-Method"));
        
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
// Ajouter cette méthode dans UserRestAPI
@GetMapping("/statistics/role")
public ResponseEntity<Map<String, Long>> getUserStatisticsByRole() {
    Map<String, Long> statistics = userService.getUserStatisticsByRole();
    return ResponseEntity.ok(statistics);
}

    @GetMapping("/statistics/total")
    public ResponseEntity<Long> getTotalUsersCount() {
        long totalUsers = userService.getTotalUsersCount();
        return ResponseEntity.ok(totalUsers);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok().body(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found with ID: " + id));
        }
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User existingUser = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
            // Update fields that are not null in the request

            if (userDetails.getUsername() != null) {
                existingUser.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }

            if (userDetails.getRole() != null) {
                existingUser.setRole(userDetails.getRole());
            }
            if (userDetails.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            }
            if (userDetails.getFirstName() != null) {
                existingUser.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                existingUser.setLastName(userDetails.getLastName());
            }
            if (userDetails.isActive() != existingUser.isActive()) {
                existingUser.setActive(userDetails.isActive());
            }
            if (userDetails.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            // Save the updated user
            User updatedUser = userService.updateUser(existingUser);
            return ResponseEntity.ok().body(updatedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }

    // Modify existing login endpoint in UserRestAPI.java
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            User loggedInUser = userService.login(credentials.get("email"), credentials.get("password"));

            // Generate JWT token
            String token = jwtUtil.generateToken(loggedInUser);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInUser);
            response.put("tokenType", "Bearer");
            response.put("message", "Login successful");

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Modified register endpoint to use JWT
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);

            // Generate JWT token
            String token = jwtUtil.generateToken(registeredUser);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", registeredUser);
            response.put("tokenType", "Bearer");
            response.put("message", "Registration successful");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "No token provided"));
    }
    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        try {
            String googleToken = request.get("token");
            User user = userService.loginWithGoogle(googleToken);

            String jwtToken = jwtUtil.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    "token", jwtToken,
                    "user", user,
                    "tokenType", "Bearer",
                    "message", "Google login successful"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

}

