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

    // Constructeur par défaut (requis par JPA)
    public User() {
        // L'initialisation des Sets est déjà faite au niveau de la déclaration des
        // champs
    }

    // Constructeur avec username
    public User(String username) {
        this.username = username;
        this.cart = new Cart(this);
    }

    // --- Méthodes Helper pour les favoris ---
    public void addFavorite(Product product) {
        if (product != null) {
            this.favoriteProducts.add(product);
            // Gérer la relation bidirectionnelle est recommandé mais omis ici comme dans
            // votre code
            // if (product.getFavoritedBy() != null) { product.getFavoritedBy().add(this); }
        }
    }

    public void removeFavorite(Product product) {
        if (product != null) {
            this.favoriteProducts.remove(product);
            // if (product.getFavoritedBy() != null) {
            // product.getFavoritedBy().remove(this); }
        }
    }

    // --- Getters et Setters (manuels) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Product> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void setFavoriteProducts(Set<Product> favoriteProducts) {
        this.favoriteProducts = favoriteProducts != null ? favoriteProducts : new HashSet<>();
    }

    // --- Getters et Setters pour Orders ---
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders != null ? orders : new HashSet<>();
    }

    public Cart getCart() {
        if (cart == null) {
            cart = new Cart(this);
        }
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // --- toString, equals, hashCode (manuels) ---

    // toString modifié pour éviter les boucles (ne pas inclure les collections
    // liées)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    // equals basé sur l'ID (commun pour les entités JPA), gère les proxies
    // Hibernate
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        // Vérifie si l'objet o peut être un proxy de User généré par Hibernate
        if (o == null || getClass() != o.getClass()
                && !o.getClass().getName().startsWith(getClass().getName() + "$$HibernateProxy")) {
            return false;
        }
        User user = (User) o;
        // Ne comparer que si l'ID n'est pas null (entité persistée)
        // Et s'assurer que les deux ID sont égaux
        return id != null && Objects.equals(id, user.getId());
    }

    // hashCode basé sur l'ID (utiliser Objects.hash ou une constante pour les ID
    // null)
    @Override
    public int hashCode() {
        // Utiliser une constante si l'ID est null (pour les objets non encore
        // persistés)
        // getClass().hashCode() pourrait être ajouté pour différencier les types si
        // nécessaire
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }
}