package esprit.microproject.Controllers;

import esprit.microproject.Entities.Product;
import esprit.microproject.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import necessary annotations

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular dev server
@RestController // Combination of @Controller and @ResponseBody, marks this as a REST controller
@RequestMapping("/api/products") // Base URL path for all methods in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products -> READ All Products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Return 200 OK with the list
    }

    // GET /api/products/{id} -> READ One Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // If found, wrap in 200 OK
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    // POST /api/products -> CREATE a new Product
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT /api/products/{id} -> UPDATE an existing Product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(ResponseEntity::ok) // If updated, return 200 OK with updated product
                .orElse(ResponseEntity.notFound().build()); // If product not found, return 404
    }

    // DELETE /api/products/{id} -> DELETE a Product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if product not found
        }
    }

    // Route pour rechercher des produits par nom
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(name = "name") String name) {
        // Vérification si le paramètre est vide
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Product> products = productService.searchProductsByName(name);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si aucun produit n'est trouvé
        }
        return ResponseEntity.ok(products); // Retourner les produits trouvés
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
}
