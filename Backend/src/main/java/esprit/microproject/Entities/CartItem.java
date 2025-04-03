package esprit.microproject.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal; // Importer BigDecimal
import java.util.Objects;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Côté propriétaire de la relation ManyToOne avec Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore // Important pour éviter boucle Cart -> CartItem -> Cart
    private Cart cart;

    // Relation vers le produit. EAGER est souvent utile ici.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Constructeur JPA requis
    public CartItem() {
    }

    // Constructeur utile
    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
    }

    // --- Getters & Setters Manuels ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Helper pour obtenir le total de la ligne (calculé)
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // --- equals, hashCode, toString (Manuels, SANS les relations complexes) ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass()
                && !o.getClass().getName().startsWith(getClass().getName() + "$$HibernateProxy")) {
            return false;
        }
        CartItem cartItem = (CartItem) o;
        // Compare basé sur l'ID si non null, ou sur panier+produit si ID est null
        if (id != null) {
            return Objects.equals(id, cartItem.id);
        } else {
            // Comparaison pour les items non persistés (potentiellement avant ajout au Set)
            return Objects.equals(cart != null ? cart.getId() : null,
                    cartItem.cart != null ? cartItem.cart.getId() : null) &&
                    Objects.equals(product != null ? product.getId() : null,
                            cartItem.product != null ? cartItem.product.getId() : null);
        }
    }

    @Override
    public int hashCode() {
        // Basé sur l'ID si disponible, sinon sur panier+produit pour cohérence avec
        // equals
        if (id != null) {
            return Objects.hash(id);
        } else {
            return Objects.hash(cart != null ? cart.getId() : null, product != null ? product.getId() : null);
        }
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}