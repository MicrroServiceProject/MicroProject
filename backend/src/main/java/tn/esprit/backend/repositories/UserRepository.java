package tn.esprit.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.backend.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}