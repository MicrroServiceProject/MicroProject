package esprit.microproject.Services;

import esprit.microproject.Entities.Product;
import esprit.microproject.Entities.User;
import esprit.microproject.Repositories.CartRepository; // <<< Import CartRepository
import esprit.microproject.Repositories.ProductRepository;
import esprit.microproject.Repositories.UserRepository;
import esprit.microproject.dto.UserCreationRequest; // <<< Import UserCreationRequest
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;                      // <<< Import Logger
import org.slf4j.LoggerFactory;             // <<< Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // <<< Add Logger

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository; // <<< Add CartRepository field

    @Autowired
    public UserService(UserRepository userRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository /* <<< Inject CartRepository */) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository; // <<< Assign CartRepository
    }

    // ===============================================
    // ===          NEW createUser Method          ===
    // ===============================================
    @Transactional
    public User createUser(UserCreationRequest request) {
        logger.info("Attempting to create user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        // Optional: Check if email already exists (add existsByEmail method to UserRepository if needed)
        // if (userRepository.existsByEmail(request.getEmail())) {
        //    logger.warn("Email already exists: {}", request.getEmail());
        //    throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        // }

        // Validate input basic checks (more robust validation can be added via @Valid)
        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                !request.getEmail().contains("@") || !request.getEmail().contains(".")) {
            logger.warn("Invalid username or email format provided for user creation.");
            throw new IllegalArgumentException("Username and a valid email are required.");
        }


        // Create new user
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail()); // Set the email

        // IMPORTANT: Ensure User entity logic correctly initializes the Cart.
        // The User constructor or getCart() method should create a new Cart(this).
        // Calling getCart() here ensures it's initialized before save if necessary.
        newUser.getCart();
        logger.debug("New User object created for username: {}", request.getUsername());


        // Save the user. Due to CascadeType.ALL on User.cart,
        // the associated new Cart will also be persisted.
        User savedUser = userRepository.save(newUser);
        logger.info("User created and saved successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }
    // ===============================================
    // ===        End of NEW createUser Method     ===
    // ===============================================


    // Helper to get user or throw exception
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username); // Add log here
                    return new EntityNotFoundException("User not found with username: " + username);
                });
    }

    // Helper to get product or throw exception
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found: ID {}", productId); // Add log here
                    return new EntityNotFoundException("Product not found with ID: " + productId);
                });
    }

    // ---- Favorite Logic ----

    @Transactional
    public void addFavorite(String username, Long productId) {
        logger.debug("Adding favorite: User={}, ProductID={}", username, productId);
        // Original logic had user creation here, which is less ideal.
        // Assume user exists or should be created via createUser endpoint.
        User user = getUserByUsername(username); // Use helper which throws if not found
        Product product = getProductById(productId);

        // Update both sides of the relationship
        user.addFavorite(product);
        // product.addFavoritedBy(user); // Only needed if you manage both sides explicitly

        // Save the user (cascading should handle the join table update)
        userRepository.save(user);
        // Saving the product might not be necessary if only the join table changes
        // productRepository.save(product);
        logger.info("Added favorite product {} for user {}", productId, username);
    }

    @Transactional
    public void removeFavorite(String username, Long productId) {
        logger.debug("Removing favorite: User={}, ProductID={}", username, productId);
        User user = getUserByUsername(username);
        Product product = getProductById(productId);

        // Update both sides of the relationship
        user.removeFavorite(product);
        // product.removeFavoritedBy(user); // Only needed if you manage both sides explicitly

        // Save the user
        userRepository.save(user);
        // productRepository.save(product);
        logger.info("Removed favorite product {} for user {}", productId, username);
    }

    @Transactional(readOnly = true)
    public Set<Product> getFavorites(String username) {
        logger.debug("Fetching favorites for user: {}", username);
        User user = getUserByUsername(username);
        // Accessing the collection within a transaction initializes it (if lazy loaded)
        Set<Product> favorites = user.getFavoriteProducts();
        logger.debug("Found {} favorites for user {}", favorites.size(), username);
        return favorites;
    }
}