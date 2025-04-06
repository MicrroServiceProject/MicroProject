package esprit.microproject.Controllers;

import esprit.microproject.Services.CartService;
import esprit.microproject.Repositories.ProductRepository;
import esprit.microproject.Repositories.UserRepository;
import esprit.microproject.Entities.Product;
import esprit.microproject.Entities.User;
import esprit.microproject.Entities.Cart;
import esprit.microproject.dto.AddToCartRequest;
import esprit.microproject.dto.CartDto;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users/{username}/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartController(CartService cartService, ProductRepository productRepository, UserRepository userRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@PathVariable String username) {
        logger.info("GET cart request received for user: {}", username);
        try {
            CartDto cart = cartService.getCart(username);
            return ResponseEntity.ok(cart);
        } catch (EntityNotFoundException e) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + username);
        } catch (Exception e) {
            logger.error("Error getting cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting cart: " + e.getMessage());
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@PathVariable String username, @RequestBody AddToCartRequest request) {
        logger.info("=== Starting addItem request ===");
        logger.info("Username: {}", username);
        logger.info("ProductId: {}", request.getProductId());
        logger.info("Quantity: {}", request.getQuantity());

        try {
            // Vérifier si l'utilisateur existe
            boolean userExists = userRepository.existsByUsername(username);
            logger.info("User exists: {}", userExists);
            if (!userExists) {
                logger.error("User not found: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found: " + username);
            }

            // Vérifier si le produit existe
            Optional<Product> productOpt = productRepository.findById(request.getProductId());
            logger.info("Product exists: {}", productOpt.isPresent());
            if (productOpt.isEmpty()) {
                logger.error("Product not found with ID: {}", request.getProductId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found with ID: " + request.getProductId());
            }

            // Vérifier si la quantité est valide
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                logger.error("Invalid quantity: {}", request.getQuantity());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Quantity must be greater than 0");
            }

            // Récupérer l'utilisateur et son panier
            User user = userRepository.findByUsername(username).get();
            logger.info("User ID: {}", user.getId());
            Cart cart = user.getCart();
            if (cart == null) {
                logger.error("Cart is null for user: {}", username);
                cart = new Cart(user);
                user = userRepository.save(user);
            }
            logger.info("Cart ID: {}", cart.getId());

            CartDto cartDto = cartService.addItem(username, request);
            logger.info("Item added successfully to cart for user: {}", username);
            return ResponseEntity.ok(cartDto);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding item to cart: {}", e.getMessage());
            e.printStackTrace(); // Pour avoir la stack trace complète dans les logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding item to cart: " + e.getMessage());
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable String username,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        logger.info("PUT update quantity request received for user: {}, productId: {}, quantity: {}",
                username, productId, quantity);
        try {
            CartDto cart = cartService.updateItemQuantity(username, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating quantity for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating quantity: " + e.getMessage());
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable String username,
            @PathVariable Long productId) {
        logger.info("DELETE item request received for user: {}, productId: {}", username, productId);
        try {
            CartDto cart = cartService.removeItem(username, productId);
            return ResponseEntity.ok(cart);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error removing item from cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing item: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(@PathVariable String username) {
        logger.info("DELETE clear cart request received for user: {}", username);
        try {
            CartDto cart = cartService.clearCart(username);
            return ResponseEntity.ok(cart);
        } catch (EntityNotFoundException e) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + username);
        } catch (Exception e) {
            logger.error("Error clearing cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error clearing cart: " + e.getMessage());
        }
    }
}