package esprit.microproject.Controllers;

import esprit.microproject.Entities.Product;
import esprit.microproject.Services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users/{username}/favorites")
public class FavoriteController {

    private final UserService userService;

    @Autowired
    public FavoriteController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserFavorites(@PathVariable String username) {
        try {
            Set<Product> favorites = userService.getFavorites(username);
            return ResponseEntity.ok(favorites);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving favorites: " + e.getMessage());
        }
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addFavorite(@PathVariable String username, @PathVariable Long productId) {
        try {
            userService.addFavorite(username, productId);
            return ResponseEntity.ok().body("Product " + productId + " added to favorites for " + username);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding favorite: " + e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable String username, @PathVariable Long productId) {
        try {
            userService.removeFavorite(username, productId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing favorite: " + e.getMessage());
        }
    }
}