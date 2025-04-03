package esprit.microproject.Repositories;

import esprit.microproject.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You might add methods later, e.g., find by user
    List<Order> findByUserUsername(String username);
}