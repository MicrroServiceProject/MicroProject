package esprit.microproject.dto;
import java.math.BigDecimal;
// Pas de Lombok
public class CartItemResponseDto {
    private Long cartItemId; // L'ID de l'objet CartItem lui-même
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice; // Prix *unitaire* actuel du produit
    private Integer quantity;
    private BigDecimal lineTotal; // Prix unitaire * quantité

    public CartItemResponseDto() {}

    // Getters et Setters pour tous les champs...
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}