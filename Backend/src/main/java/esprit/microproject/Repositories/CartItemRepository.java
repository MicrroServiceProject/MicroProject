package esprit.microproject.Repositories;
import esprit.microproject.Entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Utile pour trouver un item spécifique pour mise à jour/suppression directe si nécessaire
    // Optional<CartItem> findByIdAndCartUserUsername(Long cartItemId, String username);
}