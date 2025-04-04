package esprit.projet_web.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationParStatut {
    private Reservation.StatutReservation statut;
    private long count;
    public ReservationParStatut(Reservation.StatutReservation statut, long count) {
        this.statut = statut;
        this.count = count;
    }
}
