package esprit.projet_web.Entity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "evenements")
public class Evenement {
    @Id
    private String id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Future(message = "La date doit être dans le futur")
    private Date date;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    private int capaciteMax;

    // Assurez-vous que le champ 'tags' est défini si vous l'utilisez
    private List<String> tags;

    private int placesReservees;

    @DBRef // Assurez-vous que l'annotation est présente
    private Artiste artiste;

    @DBRef // Si vous voulez lier directement à l'entité Reservation
    private List<Reservation> reservations;

    // --- NOUVEAU CHAMP ---
    // Champ pour stocker les IDs des produits associés (du service 'MicroProject')
    private Set<Long> productIds = new HashSet<>(); // Utiliser Long car l'ID de Product est Long

    // Constructeurs
    public Evenement() {
        // Initialiser les collections est une bonne pratique
        this.reservations = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.productIds = new HashSet<>(); // Initialiser aussi ici
    }

    // Getters et Setters pour tous les champs

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }

    public List<String> getTags() {
        // Initialise si null pour éviter NullPointerException.
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        return tags;
    }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getPlacesReservees() { return placesReservees; }
    public void setPlacesReservees(int placesReservees) { this.placesReservees = placesReservees; }

    public Artiste getArtiste() { return artiste; }
    public void setArtiste(Artiste artiste) { this.artiste = artiste; }

    public List<Reservation> getReservations() {
        // Initialise si null pour éviter NullPointerException.
        if (this.reservations == null) {
            this.reservations = new ArrayList<>();
        }
        return reservations;
    }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    // Getters et Setters pour le nouveau champ productIds
    public Set<Long> getProductIds() {
        // Initialise si null pour éviter NullPointerException.
        if (this.productIds == null) {
            this.productIds = new HashSet<>();
        }
        return productIds;
    }
    public void setProductIds(Set<Long> productIds) { this.productIds = productIds; }

    // Méthodes utilitaires (getPlacesDisponibles, incrementer/decrementer)
    public int getPlacesDisponibles() {
        return this.capaciteMax - this.placesReservees;
    }

    public void incrementerPlacesReservees(int nombre) {
        if (this.placesReservees + nombre > this.capaciteMax) {
            throw new IllegalStateException("Capacité maximale dépassée pour l'événement '" + this.nom + "' (ID: " + this.id + ")");
        }
        this.placesReservees += nombre;
    }

    public void decrementerPlacesReservees(int nombre) {
        if (this.placesReservees - nombre < 0) {
            this.placesReservees = 0; // Option: éviter un nombre négatif
            // Ou lancer une exception selon la logique métier :
            // throw new IllegalStateException("Tentative de décrémenter les places réservées en dessous de zéro pour l'événement '" + this.nom + "'");
        } else {
            this.placesReservees -= nombre;
        }
    }
}