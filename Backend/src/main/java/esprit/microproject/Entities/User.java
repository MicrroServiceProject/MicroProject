package esprit.microproject.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    // *** ADD THIS EMAIL FIELD ***
    @Column(nullable = true) // Or false if email is mandatory
    private String email;
    // **************************

    // --- Relation vers les produits favoris ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorite_products", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Set<Product> favoriteProducts = new HashSet<>();

    // --- Relation vers les commandes (Orders) ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Cart cart;

    public User() {
        // Initialize cart in constructor or getter
        this.cart = new Cart(this);
    }

    public User(String username) {
        this.username = username;
        this.cart = new Cart(this);
    }

    // *** ADD GETTER AND SETTER FOR EMAIL ***
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // ***************************************

    // --- Méthodes Helper pour les favoris ---
    public void addFavorite(Product product) {
        if (product != null) {
            this.favoriteProducts.add(product);
        }
    }

    public void removeFavorite(Product product) {
        if (product != null) {
            this.favoriteProducts.remove(product);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Set<Product> getFavoriteProducts() { return favoriteProducts; }
    public void setFavoriteProducts(Set<Product> favoriteProducts) { this.favoriteProducts = favoriteProducts != null ? favoriteProducts : new HashSet<>(); }
    public Set<Order> getOrders() { return orders; }
    public void setOrders(Set<Order> orders) { this.orders = orders != null ? orders : new HashSet<>(); }
    public Cart getCart() {
        // Ensure cart is initialized if accessed before constructor in some JPA scenarios
        if (cart == null) {
            cart = new Cart(this);
        }
        return cart;
    }
    public void setCart(Cart cart) { this.cart = cart; }

    // --- toString, equals, hashCode ---
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() && !o.getClass().getName().startsWith(getClass().getName() + "$$HibernateProxy")) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }
}