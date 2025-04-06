package esprit.microproject.Services;

import esprit.microproject.Entities.*;
import esprit.microproject.Repositories.*;
import esprit.microproject.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;                       // Import Logger
import org.slf4j.LoggerFactory;              // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class); // Add logger

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService; // <<< Inject EmailService

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        EmailService emailService /* <<< Add EmailService to constructor */ ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.emailService = emailService; // <<< Assign injected service
    }

    @Transactional // Ensure all operations happen in one transaction
    public Order placeOrder(String username, PlaceOrderRequest request) {
        logger.info("Placing order for user: {}", username); // Add logging

        // 1. Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found during order placement: {}", username);
                    return new EntityNotFoundException("User not found: " + username);
                });
        logger.debug("User found: {}", user.getId());

        // 2. Create a new Order entity
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process each item in the request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            logger.warn("Order request for user {} is empty.", username);
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
        logger.debug("Processing {} items for order", request.getItems().size());

        for (CartItemDto itemDto : request.getItems()) {
            if (itemDto.getProductId() == null) {
                logger.warn("Skipping item with null productId in order for user {}", username);
                continue; // Or throw exception
            }
            // 3a. Find the product
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> {
                        logger.error("Product not found during order placement: ID {}", itemDto.getProductId());
                        return new EntityNotFoundException("Product not found: ID " + itemDto.getProductId());
                    });
            logger.trace("Found product {} for order item", product.getId());

            // 3b. TODO: Implement stock check here if needed

            // 3c. Create OrderItem
            int quantity = itemDto.getQuantity() != null && itemDto.getQuantity() > 0 ? itemDto.getQuantity() : 1;
            BigDecimal itemPrice = product.getPrice(); // Price at the time of order
            OrderItem orderItem = new OrderItem(order, product, quantity, itemPrice);
            logger.trace("Created order item: ProductId={}, Qty={}, Price={}", product.getId(), quantity, itemPrice);

            // 3d. Add item to order (using helper method)
            order.addOrderItem(orderItem);

            // 3e. Update total amount
            totalAmount = totalAmount.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));

            // 3f. TODO: Decrement stock here if needed
        }

        // 4. Set final total amount
        order.setTotalAmount(totalAmount);
        logger.debug("Calculated total amount: {}", totalAmount);

        // 5. Save the Order (OrderItems will be saved due to CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved successfully with ID: {} for user: {}", savedOrder.getId(), username);


        // 6. <<< SEND CONFIRMATION EMAIL (Asynchronously) >>>
        try {
            logger.debug("Attempting to send order confirmation email for Order ID: {}", savedOrder.getId());
            emailService.sendOrderConfirmationEmail(savedOrder); // Call the async method
            logger.debug("Email sending task submitted for Order ID: {}", savedOrder.getId());
        } catch (Exception e) {
            // Log error but DO NOT throw exception here, as the order IS saved.
            // The @Async method handles its own errors internally.
            logger.error("Error submitting order confirmation email task for Order ID {}: {}", savedOrder.getId(), e.getMessage(), e);
            // You might want to flag the order for manual notification check
        }
        // <<< END OF EMAIL SENDING >>>

        // 7. TODO: Clear the user's cart (implement cart logic separately if needed)
        // Example: cartService.clearCart(username);

        return savedOrder; // Return the saved order details
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(String username) {
        logger.debug("Fetching orders for user: {}", username);
        // Ensure user exists first if needed, or let repository handle it
        if (!userRepository.existsByUsername(username)) {
            logger.warn("Attempted to fetch orders for non-existent user: {}", username);
            throw new EntityNotFoundException("User not found: " + username); // Or return empty list
        }
        return orderRepository.findByUserUsername(username);
    }
}