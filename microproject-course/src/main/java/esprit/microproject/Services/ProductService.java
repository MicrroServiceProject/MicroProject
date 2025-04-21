package esprit.microproject.Services;

import esprit.microproject.Entities.Product;
import esprit.microproject.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Collections; // Importer Collections

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // READ All (Inchangé)
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ One by ID (Inchangé et Confirmé)
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // --- AJOUT OPTIONNEL MAIS RECOMMANDÉ ---
    /**
     * Récupère une liste de produits par leurs IDs.
     * @param ids Liste des IDs de produits.
     * @return Liste des produits trouvés.
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findAllById(ids); // Utilise la méthode standard de JpaRepository
    }
    // ----------------------------------------

    // CREATE (Inchangé)
    @Transactional
    public Product createProduct(Product product) {
        // Vérifie si un produit avec le même nom existe déjà (logique simple, peut être affinée)
        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Un produit avec le nom '" + product.getName() + "' existe déjà !");
        }
        return productRepository.save(product);
    }

    // UPDATE (Inchangé)
    @Transactional
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setImageUrl(productDetails.getImageUrl());
                    existingProduct.setCategory(productDetails.getCategory());
                    // Ajoutez ici d'autres champs à mettre à jour si nécessaire
                    return productRepository.save(existingProduct);
                }); // Retourne un Optional<Product>
    }

    // DELETE (Inchangé)
    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Méthode de recherche dynamique (Inchangé)
    @Transactional(readOnly = true) // Bonne pratique d'ajouter readOnly pour les recherches
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}