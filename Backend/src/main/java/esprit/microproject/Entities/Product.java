package esprit.microproject.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // Make sure imports are from jakarta.persistence

import java.math.BigDecimal; // Use BigDecimal for currency
import java.util.HashSet;
import java.util.Set;

@Entity // Marks this class as a JPA entity (a table in the DB)
@Table(name = "products") // Specifies the table name (optional, defaults to class name)
public class Product {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID value
    private Long id; // Use Long for IDs usually

    @Column(nullable = false) // Database column cannot be null
    private String name;

    @Lob // Specifies that this should be mapped as a Large Object (for longer text)
    @Column(nullable = false, columnDefinition = "TEXT") // Use TEXT type for longer descriptions
    private String description;

    @Column(nullable = false, precision = 10, scale = 2) // Precision for currency
    private BigDecimal price; // Use BigDecimal for precise monetary values

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String category; // Keep as String for 'tools' or 'paintings'
    // Could also use an Enum later for more type safety
    // --- Inverse Relationship to Users who favorited this product ---
    @ManyToMany(mappedBy = "favoriteProducts", fetch = FetchType.LAZY)
    @JsonIgnore // VERY IMPORTANT: Prevents infinite loop during JSON serialization
    private Set<User> favoritedBy = new HashSet<>();

    // Default constructor
    public Product() {
        this.favoritedBy = new HashSet<>();
    }

    // Helper methods for managing the bidirectional relationship
    public void addFavoritedBy(User user) {
        this.favoritedBy.add(user);
    }

    public void removeFavoritedBy(User user) {
        this.favoritedBy.remove(user);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    // Override toString to avoid circular references
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }

    // Override equals and hashCode to avoid circular references
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return id != null && id.equals(product.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}