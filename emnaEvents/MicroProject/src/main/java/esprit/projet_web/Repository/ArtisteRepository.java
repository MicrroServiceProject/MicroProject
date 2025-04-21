package esprit.projet_web.Repository;

import esprit.projet_web.Entity.Artiste;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ArtisteRepository extends MongoRepository<Artiste, String> {
    Optional<Artiste> findByEmail(String email);
    boolean existsByEmail(String email);
}