package esprit.projet_web.Repository;

import esprit.projet_web.Entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String>,ReservationRepositoryCustom {
    List<Reservation> findByStatut(Reservation.StatutReservation statut);
    List<Reservation> findByEvenementId(String evenementId);
    List<Reservation> findByClientId(String clientId);
    long countByEvenementIdAndStatut(String evenementId, Reservation.StatutReservation statut);
    @Query(value = "{ 'statut': 'ACCEPTE' }", fields = "{ 'nbrPlace' : 1 }")
    List<Reservation> findAllAcceptedForRevenue();

    default Double calculateTotalRevenue() {
        return findAllAcceptedForRevenue().stream()
                .mapToDouble(Reservation::getNbrPlace)
                .sum() * 20; // Supposons 20dt par place
    }
}