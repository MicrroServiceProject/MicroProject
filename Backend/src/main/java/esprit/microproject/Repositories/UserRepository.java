package esprit.microproject.Repositories;

import esprit.microproject.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Method to find a user by their username
    Optional<User> findByUsername(String username);

    // Method to check if a user exists by their username
    boolean existsByUsername(String username);
}