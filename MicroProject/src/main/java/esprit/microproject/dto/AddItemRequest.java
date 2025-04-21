package esprit.microproject.dto;
// Pas de Lombok, getters/setters manuels ou records (Java 14+)
public class AddItemRequest {
    private Long productId;
    private Integer quantity = 1; // Quantité par défaut

    public AddItemRequest() {} // Constructeur par défaut

    // Getters et Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}