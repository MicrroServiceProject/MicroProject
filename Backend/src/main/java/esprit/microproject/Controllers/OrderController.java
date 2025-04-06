package esprit.microproject.Controllers;

import esprit.microproject.Entities.Order;
import esprit.microproject.Services.OrderService;
import esprit.microproject.dto.PlaceOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Good for REST exceptions

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200") // Allow requests from frontend
@RestController
@RequestMapping("/api/users/{username}/orders") // Base path for orders related to a user
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class); // Add Logger
    private final OrderService orderService;

    // The hardcoded username check has been REMOVED.
    // IMPORTANT: This means NO security check is performed here.
    // In a real application, add Spring Security to verify the
    // logged-in user matches the {username} in the path or has admin rights.
    // private static final String TEST_USERNAME = "testuser"; // <-- REMOVED/COMMENTED

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/users/{username}/orders -> Place a new order
    @PostMapping
    public ResponseEntity<?> placeOrder(@PathVariable String username, @RequestBody PlaceOrderRequest request) {
        log.info("Received request to place order for user: {}", username);

        // --- The hardcoded check against TEST_USERNAME has been removed ---

        try {
            // Now calls orderService.placeOrder directly with the username from the path
            Order createdOrder = orderService.placeOrder(username, request);
            log.info("Order successfully placed with ID: {} for user: {}", createdOrder.getId(), username);
            // Return 201 Created with the created order details
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

        } catch (EntityNotFoundException e) {
            // Product or User not found
            log.warn("Order placement failed for user {}: {}", username, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e); // Return 404

        } catch (IllegalArgumentException e) {
            // e.g., Empty order items or other validation errors from service
            log.warn("Invalid order request for user {}: {}", username, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e); // Return 400

        } catch (Exception e) {
            // Catch other unexpected errors
            log.error("Unexpected error placing order for user {}: {}", username, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error placing order", e); // Return 500
        }
    }

    // GET /api/users/{username}/orders -> Get all orders for a user
    @GetMapping
    public ResponseEntity<?> getOrders(@PathVariable String username) {
        log.info("Received request to get orders for user: {}", username);

        // --- The hardcoded check against TEST_USERNAME has been removed ---

        try {
            List<Order> orders = orderService.getOrdersForUser(username);
            log.info("Retrieved {} orders for user: {}", orders.size(), username);
            return ResponseEntity.ok(orders);

        } catch (EntityNotFoundException e) {
            // User not found by service
            log.warn("Failed to get orders for user {}: {}", username, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e); // Return 404

        } catch (Exception e) {
            log.error("Unexpected error retrieving orders for user {}: {}", username, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders", e); // Return 500
        }
    }

    // GET /api/users/{username}/orders/{orderId} -> Get a specific order (Example Structure)
    // You would need corresponding methods in OrderService and OrderRepository
    /*
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String username, @PathVariable Long orderId) {
        log.info("Received request to get order ID {} for user: {}", orderId, username);
        try {
            // Example: Assume service method exists
            // Order order = orderService.getOrderByIdForUser(username, orderId);
            // return ResponseEntity.ok(order);
            throw new UnsupportedOperationException("getOrderById endpoint not yet implemented");
        } catch (EntityNotFoundException e) {
             log.warn("Order ID {} not found for user {}: {}", orderId, username, e.getMessage());
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
             log.error("Error retrieving order ID {} for user {}: {}", orderId, username, e.getMessage(), e);
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving order", e);
        }
    }
    */

}