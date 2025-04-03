package esprit.microproject.dto;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList; // Import ArrayList

// Pas de Lombok
public class CartResponseDto {
    private Long cartId;
    private String username;
    private List<CartItemResponseDto> items = new ArrayList<>(); // Initialiser
    private Integer totalItemsCount; // Somme des quantités
    private BigDecimal subtotal; // Somme des lineTotal

    public CartResponseDto() {}

    // Getters et Setters pour tous les champs...
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<CartItemResponseDto> getItems() { return items; }
    public void setItems(List<CartItemResponseDto> items) { this.items = items != null ? items : new ArrayList<>(); }
    public Integer getTotalItemsCount() { return totalItemsCount; }
    public void setTotalItemsCount(Integer totalItemsCount) { this.totalItemsCount = totalItemsCount; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}