package esprit.microproject;

import esprit.microproject.Entities.Product;
import esprit.microproject.Entities.User;
import esprit.microproject.Repositories.ProductRepository;
import esprit.microproject.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MicroProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, ProductRepository productRepository) {
        return args -> {
            // Create test user if it doesn't exist
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User testUser = new User("testuser");
                userRepository.save(testUser);
                System.out.println("Test user created successfully");
            }

            // Print existing products from your database
            System.out.println("\n=== Products in Your Database ===");
            productRepository.findAll().forEach(product -> {
                System.out.println("\nProduct Details:");
                System.out.println("ID: " + product.getId());
                System.out.println("Name: " + product.getName());
                System.out.println("Category: " + product.getCategory());
                System.out.println("Price: " + product.getPrice());
                System.out.println("Description: " + product.getDescription());
                System.out.println("Image URL: " + product.getImageUrl());
            });
            System.out.println("\n=== End of Products List ===\n");
        };
    }
}
