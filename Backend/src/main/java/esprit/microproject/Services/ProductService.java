package esprit.microproject.Services;

import esprit.microproject.Entities.Product;
import esprit.microproject.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;
import java.util.Optional;

@Service // Marks this as a Spring service bean
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired // Injects the ProductRepository instance
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // READ All
    @Transactional(readOnly = true) // Good practice for read operations
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ One by ID
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        // findById returns an Optional<Product> to handle cases where the ID doesn't exist
        return productRepository.findById(id);
    }

    // CREATE
    @Transactional
    public Product createProduct(Product product) {
        // Vérifier si un produit avec le même nom existe déjà
        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Le produit existe déjà !");
        }
        return productRepository.save(product);
    }

    // UPDATE
    @Transactional
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id) // Check if product exists
                .map(existingProduct -> { // If it exists, update its fields
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setImageUrl(productDetails.getImageUrl());
                    existingProduct.setCategory(productDetails.getCategory());
                    return productRepository.save(existingProduct); // Save the updated product
                }); // If findById returned empty, map does nothing, returning Optional.empty()
    }

    // DELETE
    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) { // Check if product exists before deleting
            productRepository.deleteById(id);
            return true; // Indicate successful deletion
        }
        return false; // Indicate product not found
    }
    // Méthode de recherche dynamique
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}