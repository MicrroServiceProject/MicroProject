package esprit.microproject.Controllers;

import esprit.microproject.Entities.Product;
import esprit.microproject.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional; // Importer Optional si ce n'est pas déjà fait

@CrossOrigin(origins = "http://localhost:4200") // Gardez si nécessaire pour le frontend
@RestController
@RequestMapping("/api/products") // <-- CONFIRMÉ: Correspond au FeignClient path
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products -> READ All Products (Inchangé)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} -> READ One Product by ID (Inchangé et Confirmé)
    @GetMapping("/{id}") // <-- CONFIRMÉ: Correspond au FeignClient @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) { // <-- CONFIRMÉ: Paramètre Long 'id'
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- AJOUT OPTIONNEL MAIS RECOMMANDÉ pour Scénario 1 ---
    /**
     * Récupère une liste de produits basée sur une liste d'IDs fournie en paramètre.
     * Ex: /api/products?ids=1,5,10
     * @param ids Liste des IDs de produits à récupérer.
     * @return ResponseEntity contenant la liste des produits trouvés.
     */
    @GetMapping(params = "ids") // Se déclenche si le paramètre 'ids' est présent
    public ResponseEntity<List<Product>> getProductsByIds(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            // Retourne une liste vide ou une erreur 400, selon la préférence
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Product> products = productService.getProductsByIds(ids); // Méthode à ajouter dans ProductService
        if (products.isEmpty()) {
            // Si aucun produit n'est trouvé pour les IDs donnés, 204 No Content ou 404 Not Found
            // sont des options. 200 OK avec liste vide est aussi acceptable.
            return ResponseEntity.ok(Collections.emptyList());
            // Ou: return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    // -------------------------------------------------------

    // POST /api/products -> CREATE Product (Inchangé)
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT /api/products/{id} -> UPDATE Product (Inchangé)
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        // Utilisation d'Optional pour gérer le cas non trouvé
        Optional<Product> updatedProductOpt = productService.updateProduct(id, productDetails);
        return updatedProductOpt
                .map(ResponseEntity::ok) // Si présent, retourne 200 OK avec le produit
                .orElse(ResponseEntity.notFound().build()); // Sinon, retourne 404 Not Found
    }


    // DELETE /api/products/{id} -> DELETE a Product (Inchangé)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content si succès
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found si l'ID n'existe pas
        }
    }

    // GET /api/products/search -> Search Products by Name (Inchangé)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(name = "name") String name) {
        if (name == null || name.trim().isEmpty()) { // Vérifier aussi les espaces vides
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Product> products = productService.searchProductsByName(name);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si rien trouvé
        }
        return ResponseEntity.ok(products);
    }

    // GET /api/products/available -> Get Available Products (Inchangé - logique métier à définir)
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        // Pour l'instant, retourne tous les produits.
        // Une logique plus complexe pourrait être ajoutée ici (ex: vérifier stock si applicable).
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
}