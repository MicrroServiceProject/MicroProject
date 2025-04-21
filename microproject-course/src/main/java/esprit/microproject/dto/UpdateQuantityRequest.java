package esprit.microproject.dto;
// Pas de Lombok
public class UpdateQuantityRequest {
    private Integer quantity;

    public UpdateQuantityRequest() {}

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}