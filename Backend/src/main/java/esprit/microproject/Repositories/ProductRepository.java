package esprit.microproject.Repositories;

import esprit.microproject.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Marks this as a Spring Data repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    List<Product> findByNameContainingIgnoreCase(String name);
    // JpaRepository<EntityType, IdType>

    // Spring Data JPA provides implementations for standard methods like:
    // - save(Product entity) -> Creates or updates a product
    // - findById(Long id) -> Finds a product by its ID
    // - findAll() -> Finds all products
    // - deleteById(Long id) -> Deletes a product by its ID
    // - count()
    // - existsById(Long id)
    // ... and more

    // You can add custom query methods here if needed later
    // Example: List<Product> findByCategory(String category);
}