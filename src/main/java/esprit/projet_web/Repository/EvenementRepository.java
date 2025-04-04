package esprit.projet_web.Repository;

import esprit.projet_web.Entity.Evenement;
import esprit.projet_web.Entity.EvenementPopulaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface EvenementRepository extends MongoRepository<Evenement, String>, EvenementRepositoryCustom {

    // Méthode paginée
    Page<Evenement> findByDateAfter(Date date, Pageable pageable);

    // Méthode non-paginée (conservée pour compatibilité)
    List<Evenement> findByDateAfter(Date date);

    // Autres méthodes existantes
    List<Evenement> findByNomContainingIgnoreCase(String nom);
    List<Evenement> findByArtisteId(String artisteId);

    @Query("{'artiste.$id': ?0, 'date': { $gt: ?1 }}")
    Page<Evenement> findByArtisteIdAndDateAfter(String artisteId, Date date, Pageable pageable);

    @Query("{'artiste.$id': { $in: ?0 }, 'date': { $gt: ?1 }}")
    Page<Evenement> findByArtisteIdInAndDateAfter(Set<String> artisteIds, Date date, Pageable pageable);

    @Query(value = "{}", fields = "{ 'capaciteMax' : 1, 'placesReservees' : 1 }")
    List<Evenement> findAllForOccupationRate();
    List<EvenementPopulaire> findTop5ByReservationsCount();
    default Double calculateAverageOccupationRate() {
        List<Evenement> events = findAllForOccupationRate();
        if (events.isEmpty()) return 0.0;

        double totalRate = events.stream()
                .filter(e -> e.getCapaciteMax() > 0)
                .mapToDouble(e -> (double) e.getPlacesReservees() / e.getCapaciteMax())
                .average()
                .orElse(0.0);

        return totalRate * 100;
    }
}