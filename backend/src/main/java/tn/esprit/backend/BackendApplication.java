package tn.esprit.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.esprit.backend.entities.User;
import tn.esprit.backend.repositories.UserRepository;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            // Check if the admin user (ghada bannouri) already exists
            User existingAdmin = userRepository.findByUsername("ghada bannouri");
            if (existingAdmin == null) {
                User admin = new User();
                admin.setUsername("ghada bannouri");
                admin.setEmail("ghada.bannouri@example.com");
                admin.setPassword("password123");
                admin.setAdmin(true);
                userRepository.save(admin);
                System.out.println("Admin user 'ghada bannouri' created successfully.");
            } else {
                System.out.println("Admin user 'ghada bannouri' already exists.");
            }

            // Check if aya bannouri exists
            User existingAya = userRepository.findByUsername("aya bannouri");
            if (existingAya == null) {
                User aya = new User();
                aya.setUsername("aya bannouri");
                aya.setEmail("aya.bannouri@example.com");
                aya.setPassword("password123");
                aya.setAdmin(false);
                userRepository.save(aya);
                System.out.println("User 'aya bannouri' created successfully.");
            } else {
                System.out.println("User 'aya bannouri' already exists.");
            }

            // Check if khalil bannouri exists
            User existingKhalil = userRepository.findByUsername("khalil bannouri");
            if (existingKhalil == null) {
                User khalil = new User();
                khalil.setUsername("khalil bannouri");
                khalil.setEmail("khalil.bannouri@example.com");
                khalil.setPassword("password123");
                khalil.setAdmin(false);
                userRepository.save(khalil);
                System.out.println("User 'khalil bannouri' created successfully.");
            } else {
                System.out.println("User 'khalil bannouri' already exists.");
            }

            // Check if the test user exists
            User existingTest = userRepository.findByUsername("test");
            if (existingTest == null) {
                User testUser = new User();
                testUser.setUsername("test");
                testUser.setEmail("test@example.com");
                testUser.setPassword("password123");
                testUser.setAdmin(false);
                userRepository.save(testUser);
                System.out.println("User 'test' created successfully.");
            } else {
                System.out.println("User 'test' already exists.");
            }
        };
    }
}