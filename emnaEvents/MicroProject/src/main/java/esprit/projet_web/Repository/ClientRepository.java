package esprit.projet_web.Repository;

import esprit.projet_web.Entity.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, String> {
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
}