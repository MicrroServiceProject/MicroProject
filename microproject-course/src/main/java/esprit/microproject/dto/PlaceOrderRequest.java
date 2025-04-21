package esprit.microproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PlaceOrderRequest {
    private List<CartItemDto> items;
}