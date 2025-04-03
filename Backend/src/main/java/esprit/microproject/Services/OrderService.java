package esprit.microproject.Services;

import esprit.microproject.Entities.*;
import esprit.microproject.Repositories.*;
import esprit.microproject.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    // OrderItemRepository is often not directly needed if using CascadeType.ALL on
    // Order

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional // Ensure all operations happen in one transaction
    public Order placeOrder(String username, PlaceOrderRequest request) {
        // 1. Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        // 2. Create a new Order entity
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process each item in the request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        for (CartItemDto itemDto : request.getItems()) {
            // 3a. Find the product
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: ID " + itemDto.getProductId()));

            // 3b. TODO: Implement stock check here if needed

            // 3c. Create OrderItem
            int quantity = itemDto.getQuantity() != null && itemDto.getQuantity() > 0 ? itemDto.getQuantity() : 1;
            BigDecimal itemPrice = product.getPrice(); // Price at the time of order
            OrderItem orderItem = new OrderItem(order, product, quantity, itemPrice);

            // 3d. Add item to order (using helper method)
            order.addOrderItem(orderItem);

            // 3e. Update total amount
            totalAmount = totalAmount.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));

            // 3f. TODO: Decrement stock here if needed
        }

        // 4. Set final total amount
        order.setTotalAmount(totalAmount);

        // 5. Save the Order (OrderItems will be saved due to CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);

        // 6. TODO: Clear the user's cart (implement cart logic separately)

        return savedOrder;
    }

    // Optional: Method to get orders for a user
    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(String username) {
        // Ensure user exists first if needed, or let repository handle it
        // userRepository.findByUsername(username).orElseThrow(...);
        return orderRepository.findByUserUsername(username);
    }
}