package esprit.projet_web.Entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;


@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;

    private Date dateReservation = new Date();

    @Min(value = 1, message = "Au moins une place doit être réservée")
    private int nbrPlace;

    private StatutReservation statut;

    @DBRef

    private Evenement evenement;

    @DBRef

    private Client client;

    public enum StatutReservation {
        ACCEPTE, REFUSE, EN_COURS, ANNULEE
    }

    public Reservation() {
        this.statut = StatutReservation.EN_COURS;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDateReservation() { return dateReservation; }
    public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }

    public int getNbrPlace() { return nbrPlace; }
    public void setNbrPlace(int nbrPlace) { this.nbrPlace = nbrPlace; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }




}