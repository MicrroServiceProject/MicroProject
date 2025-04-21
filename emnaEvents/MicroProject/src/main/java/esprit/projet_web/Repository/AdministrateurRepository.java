package esprit.projet_web.Repository;

import esprit.projet_web.Entity.Administrateur;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AdministrateurRepository extends MongoRepository<Administrateur, String> {
    Optional<Administrateur> findByEmail(String email);
    boolean existsByEmail(String email);
}