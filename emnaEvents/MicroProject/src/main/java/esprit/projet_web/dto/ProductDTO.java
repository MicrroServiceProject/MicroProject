// Créez un package dto : src/main/java/esprit/projet_web/dto/
package esprit.projet_web.dto;

import java.math.BigDecimal;

// Pas d'annotations @Document ou @Entity ici !
// Utilisez Lombok ou générez manuellement getters/setters/constructeur
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String category;

    // Constructeur sans arguments (requis pour la désérialisation JSON)
    public ProductDTO() {}

    // Getters et Setters pour tous les champs...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}