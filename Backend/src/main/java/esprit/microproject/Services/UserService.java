package esprit.microproject.Services;

import esprit.microproject.Entities.Product;
import esprit.microproject.Entities.User;
import esprit.microproject.Repositories.ProductRepository;
import esprit.microproject.Repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException; // Import EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Helper to get user or throw exception (replace with real auth later)
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    // Helper to get product or throw exception
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    // ---- Favorite Logic ----

    @Transactional
    public void addFavorite(String username, Long productId) {
        // For testing: create the user if they don't exist
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            System.out.println("Creating test user: " + username); // Log for testing
            User newUser = new User();
            newUser.setUsername(username);
            return userRepository.save(newUser);
        });

        Product product = getProductById(productId);

        // Update both sides of the relationship
        user.addFavorite(product);
        product.addFavoritedBy(user);

        // Save both entities
        userRepository.save(user);
        productRepository.save(product);
    }

    @Transactional
    public void removeFavorite(String username, Long productId) {
        User user = getUserByUsername(username);
        Product product = getProductById(productId);

        // Update both sides of the relationship
        user.removeFavorite(product);
        product.removeFavoritedBy(user);

        // Save both entities
        userRepository.save(user);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Set<Product> getFavorites(String username) {
        User user = getUserByUsername(username);
        // Accessing the collection within a transaction initializes it (if lazy loaded)
        return user.getFavoriteProducts();
    }
}