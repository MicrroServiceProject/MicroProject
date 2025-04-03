package esprit.microproject.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Côté propriétaire de la relation OneToOne avec User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Collection d'items DANS ce panier. Cart est le propriétaire de la relation
    // avec CartItem
    // CascadeType.ALL: Opérations sur Cart (save, delete) propagent à CartItem
    // orphanRemoval=true: Retirer un CartItem de ce Set le supprime de la BDD lors
    // du save/flush de Cart
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>(); // Initialisation

    @UpdateTimestamp // Se met à jour à chaque modification de l'entité Cart
    private LocalDateTime lastUpdated;

    // Constructeur JPA requis
    public Cart() {
    }

    // Constructeur utile
    public Cart(User user) {
        this.user = user;
    }

    // --- Getters & Setters Manuels ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<CartItem> getItems() {
        return items;
    }

    public void setItems(Set<CartItem> items) {
        this.items = items;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // --- Logique métier DANS l'entité (alternative à tout mettre dans le service)
    // ---

    // Méthode pour ajouter ou mettre à jour un item
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    // Méthode pour supprimer un item basé sur l'ID du produit
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    // Méthode pour mettre à jour la quantité (ou supprimer si <= 0)
    public void updateItemQuantity(Long productId, int quantity) {
        Optional<CartItem> itemOpt = this.items.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isPresent()) {
            if (quantity > 0) {
                itemOpt.get().setQuantity(quantity);
            } else {
                // Si quantité <= 0, on retire l'item
                this.items.remove(itemOpt.get());
            }
        }
        // Si l'item n'existe pas pour ce produit, on ne fait rien
    }

    public void clear() {
        items.clear();
    }

    // --- equals, hashCode, toString (Manuels, SANS les collections/relations
    // complexes) ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass()
                && !o.getClass().getName().startsWith(getClass().getName() + "$$HibernateProxy")) {
            return false;
        }
        Cart cart = (Cart) o;
        return id != null && Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", items=" + items +
                '}';
    }
}