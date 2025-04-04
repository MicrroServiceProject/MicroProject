package esprit.projet_web.Controller;

import esprit.projet_web.Entity.*;
import esprit.projet_web.Exception.BusinessException;
import esprit.projet_web.Repository.ClientRepository;
import esprit.projet_web.Service.GestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date; // Utilisez ceci plutôt que java.sql.Date

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GestionController {

    @Autowired
    private GestionService gestionService;
    @Autowired private ClientRepository clientRepository;
    // ==================================== ARTISTES ====================================
    @PostMapping("/artistes")
    public ResponseEntity<Artiste> creerArtiste(@Valid @RequestBody Artiste artiste) {
        return new ResponseEntity<>(gestionService.creerArtiste(artiste), HttpStatus.CREATED);
    }

    @GetMapping("/artistes")
    public ResponseEntity<List<Artiste>> obtenirTousArtistes() {
        return ResponseEntity.ok(gestionService.obtenirTousArtistes());
    }

    @GetMapping("/artistes/{id}")
    public ResponseEntity<Artiste> obtenirArtisteParId(@PathVariable String id) {
        return ResponseEntity.ok(gestionService.obtenirArtisteParId(id)
                .orElseThrow(() -> new RuntimeException("Artiste non trouvé")));
    }

    @PutMapping("/artistes/{id}")
    public ResponseEntity<Artiste> mettreAJourArtiste(
            @PathVariable String id,
            @Valid @RequestBody Artiste artiste) {
        return ResponseEntity.ok(gestionService.mettreAJourArtiste(id, artiste));
    }

    @DeleteMapping("/artistes/{id}")
    public ResponseEntity<Void> supprimerArtiste(@PathVariable String id) {
        gestionService.supprimerArtiste(id);
        return ResponseEntity.noContent().build();
    }

    // ==================================== EVENEMENTS ====================================
    @PostMapping("/artistes/{artisteId}/evenements")
    public ResponseEntity<Evenement> creerEvenement(
            @PathVariable String artisteId,
            @Valid @RequestBody Evenement evenement) {
        return new ResponseEntity<>(
                gestionService.creerEvenement(evenement, artisteId),
                HttpStatus.CREATED);
    }

    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> obtenirTousEvenements() {
        return ResponseEntity.ok(gestionService.obtenirTousEvenements());
    }

    @GetMapping("/evenements/avenir")
    public ResponseEntity<List<Evenement>> obtenirEvenementsAVenir() {
        return ResponseEntity.ok(gestionService.obtenirEvenementsAVenir());
    }

    @GetMapping("/evenements/{id}")
    public ResponseEntity<Evenement> obtenirEvenementParId(@PathVariable String id) {
        return ResponseEntity.ok(gestionService.obtenirEvenementParId(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé")));
    }

    @PutMapping("/evenements/{id}")
    public ResponseEntity<Evenement> mettreAJourEvenement(
            @PathVariable String id,
            @Valid @RequestBody Evenement evenement) {
        return ResponseEntity.ok(gestionService.mettreAJourEvenement(id, evenement));
    }

    @DeleteMapping("/evenements/{id}")
    public ResponseEntity<Void> supprimerEvenement(@PathVariable String id) {
        gestionService.supprimerEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artistes/{artisteId}/evenements")
    public ResponseEntity<List<Evenement>> obtenirEvenementsParArtiste(
            @PathVariable String artisteId) {
        return ResponseEntity.ok(gestionService.obtenirEvenementsParArtiste(artisteId));
    }

    // ==================================== CLIENTS ====================================
    @PostMapping("/clients")
    public ResponseEntity<Client> creerClient(@Valid @RequestBody Client client) {
        return new ResponseEntity<>(gestionService.creerClient(client), HttpStatus.CREATED);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> obtenirTousClients() {
        return ResponseEntity.ok(gestionService.obtenirTousClients());
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> obtenirClientParId(@PathVariable String id) {
        return ResponseEntity.ok(gestionService.obtenirClientParId(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé")));
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<Client> mettreAJourClient(
            @PathVariable String id,
            @Valid @RequestBody Client client) {
        return ResponseEntity.ok(gestionService.mettreAJourClient(id, client));
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> supprimerClient(@PathVariable String id) {
        gestionService.supprimerClient(id);
        return ResponseEntity.noContent().build();
    }

    // ==================================== ADMINISTRATEURS ====================================
    @PostMapping("/administrateurs")
    public ResponseEntity<Administrateur> creerAdministrateur(
            @Valid @RequestBody Administrateur administrateur) {
        return new ResponseEntity<>(
                gestionService.creerAdministrateur(administrateur),
                HttpStatus.CREATED);
    }

    @GetMapping("/administrateurs")
    public ResponseEntity<List<Administrateur>> obtenirTousAdministrateurs() {
        return ResponseEntity.ok(gestionService.obtenirTousAdministrateurs());
    }

    @GetMapping("/administrateurs/{id}")
    public ResponseEntity<Administrateur> obtenirAdministrateurParId(
            @PathVariable String id) {
        return ResponseEntity.ok(gestionService.obtenirAdministrateurParId(id)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé")));
    }

    @PutMapping("/administrateurs/{id}")
    public ResponseEntity<Administrateur> mettreAJourAdministrateur(
            @PathVariable String id,
            @Valid @RequestBody Administrateur administrateur) {
        return ResponseEntity.ok(gestionService.mettreAJourAdministrateur(id, administrateur));
    }

    @DeleteMapping("/administrateurs/{id}")
    public ResponseEntity<Void> supprimerAdministrateur(@PathVariable String id) {
        gestionService.supprimerAdministrateur(id);
        return ResponseEntity.noContent().build();
    }

    // ==================================== RESERVATIONS ====================================
    @PostMapping("/clients/{clientId}/reservations/evenements/{evenementId}")
    public ResponseEntity<Reservation> creerReservation(
            @PathVariable String clientId,
            @PathVariable String evenementId,
            @Valid @RequestBody Reservation reservation) {
        return new ResponseEntity<>(
                gestionService.creerReservation(reservation, clientId, evenementId),
                HttpStatus.CREATED);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> obtenirToutesReservations() {
        return ResponseEntity.ok(gestionService.obtenirToutesReservations());
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<Reservation> obtenirReservationParId(@PathVariable String id) {
        return ResponseEntity.ok(gestionService.obtenirReservationParId(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée")));
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> mettreAJourReservation(
            @PathVariable String id,
            @Valid @RequestBody Reservation reservation) {
        return ResponseEntity.ok(gestionService.mettreAJourReservation(id, reservation));
    }

    @PutMapping("/reservations/{id}/statut")
    public ResponseEntity<Reservation> modifierStatutReservation(
            @PathVariable String id,
            @RequestParam Reservation.StatutReservation statut) {
        return ResponseEntity.ok(gestionService.modifierStatutReservation(id, statut));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable String id) {
        gestionService.supprimerReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clients/{clientId}/reservations")
    public ResponseEntity<List<Reservation>> obtenirReservationsParClient(
            @PathVariable String clientId) {
        return ResponseEntity.ok(gestionService.obtenirReservationsParClient(clientId));
    }

    @GetMapping("/evenements/{evenementId}/reservations")
    public ResponseEntity<List<Reservation>> obtenirReservationsParEvenement(
            @PathVariable String evenementId) {
        return ResponseEntity.ok(gestionService.obtenirReservationsParEvenement(evenementId));
    }
//methode avancé
@GetMapping("/evenements/recherche")
public ResponseEntity<List<Evenement>> rechercherEvenements(
        @RequestParam(required = false) String nom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate dateDebut,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        @RequestParam(required = false) String artisteId,
        @RequestParam(required = false) Integer capaciteMin) {

    return ResponseEntity.ok(
            gestionService.rechercherEvenementsAvances(
                    nom,
                    dateDebut != null ? Date.from(dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                    dateFin != null ? Date.from(dateFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                    artisteId,
                    capaciteMin
            )
    );
}
    @PostMapping("/reservations/{id}/annulation")
    public ResponseEntity<?> annulerReservation(@PathVariable String id) {
        try {
            Reservation reservationAnnulee = gestionService.annulerReservation(id);
            return ResponseEntity.ok(reservationAnnulee);
        } catch (BusinessException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
            problemDetail.setType(URI.create("https://your-api.com/errors/reservation-error"));
            problemDetail.setTitle("Erreur d'annulation");
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("errorCategory", "reservation");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
        } catch (RuntimeException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    e.getMessage().contains("non trouvée") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            problemDetail.setType(URI.create("https://your-api.com/errors/reservation-error"));
            problemDetail.setTitle("Erreur inattendue");
            problemDetail.setProperty("timestamp", Instant.now());

            return ResponseEntity.of(problemDetail).build();
        }
    }
    @GetMapping("/statistiques")
    public ResponseEntity<?> obtenirStatistiques() {
        try {
            Map<String, Object> stats = gestionService.obtenirStatistiques();
            return ResponseEntity.ok(stats);
        } catch (BusinessException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            problemDetail.setType(URI.create("https://your-api.com/errors/statistics-error"));
            problemDetail.setTitle("Erreur de calcul des statistiques");
            problemDetail.setProperty("timestamp", Instant.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }
    @GetMapping("/clients/{clientId}/recommandations")
    public ResponseEntity<?> obtenirRecommandations(
            @PathVariable String clientId,
            @RequestParam(required = false, defaultValue = "5") int limit) {

        try {
            // 1. Vérifier que le client existe
            if (!clientRepository.existsById(clientId)) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        "Client non trouvé avec l'ID: " + clientId
                );
                problemDetail.setTitle("Client non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
            }

            // 2. Obtenir les recommandations
            List<Evenement> recommandations = gestionService.recommanderEvenements(clientId);

            // 3. Formater la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("clientId", clientId);
            response.put("dateRecommandation", new Date());
            response.put("nombreRecommandations", recommandations.size());
            response.put("recommandations", recommandations);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la génération des recommandations: " + e.getMessage()
            );
            problemDetail.setTitle("Erreur de recommandation");
            return ResponseEntity.internalServerError().body(problemDetail);
        }
    }
}
