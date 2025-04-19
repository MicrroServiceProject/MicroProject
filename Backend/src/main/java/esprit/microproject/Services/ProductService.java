package esprit.microproject.Services;

import esprit.microproject.Entities.Product;
import esprit.microproject.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // READ All
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ One by ID
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {

        return productRepository.findById(id);
    }

    // CREATE
    @Transactional
    public Product createProduct(Product product) {

        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Le produit existe déjà !");
        }
        return productRepository.save(product);
    }

    // UPDATE
    @Transactional
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setImageUrl(productDetails.getImageUrl());
                    existingProduct.setCategory(productDetails.getCategory());
                    return productRepository.save(existingProduct);
                });
    }

    // DELETE
    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    // Méthode de recherche dynamique
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}