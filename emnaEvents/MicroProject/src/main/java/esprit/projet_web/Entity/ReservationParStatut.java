package esprit.projet_web.Entity;

public class ReservationParStatut {
    private Reservation.StatutReservation statut;
    private long count;

    // Constructeur
    public ReservationParStatut(Reservation.StatutReservation statut, long count) {
        this.statut = statut;
        this.count = count;
    }

    // Getters manuels
    public Reservation.StatutReservation getStatut() {
        return statut;
    }

    public long getCount() {
        return count;
    }

    // Setters si besoin
    public void setStatut(Reservation.StatutReservation statut) {
        this.statut = statut;
    }

    public void setCount(long count) {
        this.count = count;
    }
}