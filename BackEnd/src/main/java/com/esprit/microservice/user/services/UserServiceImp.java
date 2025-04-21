package com.esprit.microservice.user.services;

import com.esprit.microservice.user.entities.GoogleUserInfo;
import com.esprit.microservice.user.entities.User;
import com.esprit.microservice.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;  // Injecting EmailService

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    @Override
    public User register(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
    
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
    
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
    
        User newUser = userRepository.save(user);
    
        // Find all admin users to notify them
        List<User> adminUsers = userRepository.findByRole(User.Role.ADMIN);
        
        if (adminUsers == null || adminUsers.isEmpty()) {
            logger.warn("No admin users found to notify about new user registration");
        } else {
            logger.info("Found {} admin users to notify about new registration", adminUsers.size());
            
            // Send email to each admin
            for (User admin : adminUsers) {
                try {
                    emailService.sendHtmlAdminNotification(
                            admin.getEmail(),
                            "New User Registration",
                            newUser.getUsername(),
                            newUser.getEmail(),
                            newUser.getFirstName(),
                            newUser.getLastName()
                    );
                    logger.info("Successfully sent new user notification to admin: {}", admin.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send notification email to admin: {}", admin.getEmail(), e);
                    // Continue with the next admin even if one fails
                }
            }
        }
    
        return newUser;
    }
    

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials or inactive account"));

        // Check if the provided password matches the stored hash
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is inactive");
        }

        return user;
    }
    // Ajoutez cette méthode dans UserServiceImp
    // Ajoutez cette méthode dans UserServiceImp
    @Override
    public User loginWithGoogle(String googleToken) {
        try {
            GoogleAuthService googleAuthService = new GoogleAuthService();
            GoogleUserInfo googleUser = googleAuthService.verifyGoogleToken(googleToken);

            return userRepository.findByGoogleId(googleUser.getGoogleId())
                    .or(() -> userRepository.findByEmail(googleUser.getEmail())
                            .map(existingUser -> {
                                // Lier le compte existant à Google
                                existingUser.setGoogleId(googleUser.getGoogleId());
                                return userRepository.save(existingUser);
                            }))
                    .orElseGet(() -> {
                        // Créer un nouvel utilisateur
                        User newUser = new User();
                        newUser.setGoogleId(googleUser.getGoogleId());
                        newUser.setEmail(googleUser.getEmail());
                        newUser.setUsername(googleUser.getEmail().split("@")[0]);
                        newUser.setFirstName(googleUser.getFirstName());
                        newUser.setLastName(googleUser.getLastName());
                        newUser.setRole(User.Role.USER);
                        newUser.setActive(true);
                        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                        return userRepository.save(newUser);
                    });
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'authentification Google: " + e.getMessage());
        }
    }
    @Override
    public Map<String, Long> getUserStatisticsByRole() {
        try {
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) {
                logger.warn("No users found in the database.");
                return Map.of(); // Return empty map if no users are found
            }
            Map<String, Long> statistics = allUsers.stream()
                    .collect(Collectors.groupingBy(user -> user.getRole().name(), Collectors.counting()));
            return statistics;
        } catch (Exception e) {
            logger.error("Failed to retrieve user statistics by role", e);
            throw new RuntimeException("Error retrieving user statistics by role", e);
        }
    }

    @Override
    public long getTotalUsersCount() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            logger.error("Failed to count total users", e);
            throw new RuntimeException("Error counting total users", e);
        }
    }


}
