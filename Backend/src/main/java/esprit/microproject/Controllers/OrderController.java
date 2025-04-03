package esprit.microproject.Controllers;

import esprit.microproject.Entities.Order;
import esprit.microproject.Services.OrderService;
import esprit.microproject.dto.PlaceOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users/{username}/orders") // Base path for orders related to a user
public class OrderController {

    private final OrderService orderService;

    // Use a fixed username for simplified testing (REMOVE FOR REAL AUTH)
    private static final String TEST_USERNAME = "testuser";

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/users/{username}/orders -> Place a new order
    @PostMapping
    public ResponseEntity<?> placeOrder(@PathVariable String username, @RequestBody PlaceOrderRequest request) {
        if (!TEST_USERNAME.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        try {
            Order createdOrder = orderService.placeOrder(username, request);
            // Return 201 Created with the created order details
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (EntityNotFoundException e) {
            // Product or User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // e.g., Empty order items
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Catch other unexpected errors
            // Log the error server-side: log.error("Error placing order:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error placing order: " + e.getMessage());
        }
    }

    // GET /api/users/{username}/orders -> Get all orders for a user
    @GetMapping
    public ResponseEntity<?> getOrders(@PathVariable String username) {
        if (!TEST_USERNAME.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        try {
            List<Order> orders = orderService.getOrdersForUser(username);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the error server-side: log.error("Error getting orders:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving orders: " + e.getMessage());
        }
    }

    // GET /api/users/{username}/orders/{orderId} -> Get a specific order (Optional)
    // You would need methods in OrderService and OrderRepository (like
    // findByIdAndUserUsername)
    // @GetMapping("/{orderId}")
    // public ResponseEntity<?> getOrderById(@PathVariable String username,
    // @PathVariable Long orderId) { ... }

}