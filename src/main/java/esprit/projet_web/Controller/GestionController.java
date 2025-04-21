package esprit.projet_web.Controller;

import esprit.projet_web.Entity.*;
import esprit.projet_web.Exception.BusinessException;
import esprit.projet_web.Service.GestionService; // Assurez-vous que GestionService est importé
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Importer Optional

@RestController
@RequestMapping("/api") // Préfixe pour toutes les routes du contrôleur
public class GestionController {

    private static final Logger logger = LoggerFactory.getLogger(GestionController.class);

    @Autowired
    private GestionService gestionService;
    // L'injection du ClientRepository ici n'est généralement pas nécessaire si le service gère toute la logique.
    // @Autowired private ClientRepository clientRepository;

    // ==================================== ARTISTES ====================================

    @PostMapping("/artistes")
    public ResponseEntity<?> creerArtiste(@Valid @RequestBody Artiste artiste) {
        try {
            Artiste nouvelArtiste = gestionService.creerArtiste(artiste);
            return new ResponseEntity<>(nouvelArtiste, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erreur création artiste", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur", e.getMessage());
        }
    }

    @GetMapping("/artistes")
    public ResponseEntity<List<Artiste>> obtenirTousArtistes() {
        List<Artiste> artistes = gestionService.obtenirTousArtistes();
        return ResponseEntity.ok(artistes);
    }

    @GetMapping("/artistes/{id}")
    public ResponseEntity<?> obtenirArtisteParId(@PathVariable String id) {
        Optional<Artiste> artisteOpt = gestionService.obtenirArtisteParId(id);
        return artisteOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "Artiste non trouvé", "Aucun artiste trouvé pour l'ID: " + id));
    }

    @PutMapping("/artistes/{id}")
    public ResponseEntity<?> mettreAJourArtiste(@PathVariable String id, @Valid @RequestBody Artiste artiste) {
        try {
            Artiste artisteMaj = gestionService.mettreAJourArtiste(id, artiste);
            return ResponseEntity.ok(artisteMaj);
        } catch (RuntimeException e) { // Inclut l'erreur "non trouvé" du service
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return buildErrorResponse(status, "Erreur MàJ artiste", e.getMessage());
        }
    }

    @DeleteMapping("/artistes/{id}")
    public ResponseEntity<Void> supprimerArtiste(@PathVariable String id) {
        try {
            gestionService.supprimerArtiste(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Gérer si l'artiste n'existe pas
            // Vous pourriez retourner 404 ici, mais noContent est souvent acceptable pour DELETE
            logger.warn("Tentative de suppression d'un artiste non trouvé: {}", id);
            return ResponseEntity.noContent().build(); // Ou ResponseEntity.notFound().build();
        }
    }

    // ==================================== EVENEMENTS ====================================

    @PostMapping("/artistes/{artisteId}/evenements")
    public ResponseEntity<?> creerEvenement(@PathVariable String artisteId, @Valid @RequestBody Evenement evenement) {
        try {
            Evenement nouvelEvenement = gestionService.creerEvenement(evenement, artisteId);
            return new ResponseEntity<>(nouvelEvenement, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("Artiste non trouvé") ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
            return buildErrorResponse(status, "Erreur création événement", e.getMessage());
        }
    }

    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> obtenirTousEvenements() {
        List<Evenement> evenements = gestionService.obtenirTousEvenements();
        return ResponseEntity.ok(evenements);
    }

    @GetMapping("/evenements/avenir")
    public ResponseEntity<List<Evenement>> obtenirEvenementsAVenir() {
        List<Evenement> evenements = gestionService.obtenirEvenementsAVenir();
        return ResponseEntity.ok(evenements);
    }

    @GetMapping("/evenements/{id}")
    public ResponseEntity<?> obtenirEvenementParId(@PathVariable String id) {
        Optional<Evenement> evenementOpt = gestionService.obtenirEvenementParId(id);
        return evenementOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "Événement non trouvé", "Aucun événement trouvé pour l'ID: " + id));
    }

    @PutMapping("/evenements/{id}")
    public ResponseEntity<?> mettreAJourEvenement(@PathVariable String id, @Valid @RequestBody Evenement evenement) {
        try {
            Evenement evenementMaj = gestionService.mettreAJourEvenement(id, evenement);
            return ResponseEntity.ok(evenementMaj);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return buildErrorResponse(status, "Erreur MàJ événement", e.getMessage());
        }
    }

    @DeleteMapping("/evenements/{id}")
    public ResponseEntity<Void> supprimerEvenement(@PathVariable String id) {
        try {
            gestionService.supprimerEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Tentative de suppression d'un événement non trouvé: {}", id);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/artistes/{artisteId}/evenements")
    public ResponseEntity<List<Evenement>> obtenirEvenementsParArtiste(@PathVariable String artisteId) {
        // Ajouter une vérification si l'artiste existe pourrait être bien
        List<Evenement> evenements = gestionService.obtenirEvenementsParArtiste(artisteId);
        return ResponseEntity.ok(evenements);
    }

    // ==================================== CLIENTS ====================================

    @PostMapping("/clients")
    public ResponseEntity<?> creerClient(@Valid @RequestBody Client client) {
        try {
            Client nouveauClient = gestionService.creerClient(client);
            return new ResponseEntity<>(nouveauClient, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erreur création client", e.getMessage());
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> obtenirTousClients() {
        return ResponseEntity.ok(gestionService.obtenirTousClients());
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<?> obtenirClientParId(@PathVariable String id) {
        Optional<Client> clientOpt = gestionService.obtenirClientParId(id);
        return clientOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "Client non trouvé", "Aucun client trouvé pour l'ID: " + id));
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<?> mettreAJourClient(@PathVariable String id, @Valid @RequestBody Client client) {
        try {
            Client clientMaj = gestionService.mettreAJourClient(id, client);
            return ResponseEntity.ok(clientMaj);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return buildErrorResponse(status, "Erreur MàJ client", e.getMessage());
        }
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> supprimerClient(@PathVariable String id) {
        try {
            gestionService.supprimerClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Tentative de suppression d'un client non trouvé: {}", id);
            return ResponseEntity.noContent().build();
        }
    }

    // ==================================== ADMINISTRATEURS ====================================

    @PostMapping("/administrateurs")
    public ResponseEntity<?> creerAdministrateur(@Valid @RequestBody Administrateur administrateur) {
        try {
            Administrateur nouvelAdmin = gestionService.creerAdministrateur(administrateur);
            return new ResponseEntity<>(nouvelAdmin, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erreur création admin", e.getMessage());
        }
    }

    @GetMapping("/administrateurs")
    public ResponseEntity<List<Administrateur>> obtenirTousAdministrateurs() {
        return ResponseEntity.ok(gestionService.obtenirTousAdministrateurs());
    }

    @GetMapping("/administrateurs/{id}")
    public ResponseEntity<?> obtenirAdministrateurParId(@PathVariable String id) {
        Optional<Administrateur> adminOpt = gestionService.obtenirAdministrateurParId(id);
        return adminOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "Admin non trouvé", "Aucun admin trouvé pour l'ID: " + id));
    }

    @PutMapping("/administrateurs/{id}")
    public ResponseEntity<?> mettreAJourAdministrateur(@PathVariable String id, @Valid @RequestBody Administrateur administrateur) {
        try {
            Administrateur adminMaj = gestionService.mettreAJourAdministrateur(id, administrateur);
            return ResponseEntity.ok(adminMaj);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return buildErrorResponse(status, "Erreur MàJ admin", e.getMessage());
        }
    }

    @DeleteMapping("/administrateurs/{id}")
    public ResponseEntity<Void> supprimerAdministrateur(@PathVariable String id) {
        try {
            gestionService.supprimerAdministrateur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Tentative de suppression d'un admin non trouvé: {}", id);
            return ResponseEntity.noContent().build();
        }
    }

    // ==================================== RESERVATIONS ====================================

    @PostMapping("/clients/{clientId}/reservations/evenements/{evenementId}")
    public ResponseEntity<?> creerReservation(@PathVariable String clientId, @PathVariable String evenementId, @Valid @RequestBody Reservation reservation) throws BusinessException, RuntimeException {
        Reservation nouvelleReservation = gestionService.creerReservation(reservation, clientId, evenementId);
        return new ResponseEntity<>(nouvelleReservation, HttpStatus.CREATED);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> obtenirToutesReservations() {
        return ResponseEntity.ok(gestionService.obtenirToutesReservations());
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<?> obtenirReservationParId(@PathVariable String id) {
        Optional<Reservation> reservationOpt = gestionService.obtenirReservationParId(id);
        return reservationOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "Réservation non trouvée", "Aucune réservation trouvée pour l'ID: " + id));
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<?> mettreAJourReservation(@PathVariable String id, @Valid @RequestBody Reservation reservation) throws BusinessException, RuntimeException {
        Reservation reservationMaj = gestionService.mettreAJourReservation(id, reservation);
        return ResponseEntity.ok(reservationMaj);
    }

    @PutMapping("/reservations/{id}/statut")
    public ResponseEntity<?> modifierStatutReservation(@PathVariable String id, @RequestParam Reservation.StatutReservation statut) throws BusinessException, RuntimeException {
        Reservation reservationMaj = gestionService.modifierStatutReservation(id, statut);
        return ResponseEntity.ok(reservationMaj);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable String id) {
        try {
            gestionService.supprimerReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Tentative de suppression d'une réservation non trouvée: {}", id);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/clients/{clientId}/reservations")
    public ResponseEntity<List<Reservation>> obtenirReservationsParClient(@PathVariable String clientId) {
        // Ajouter vérification existence client si nécessaire
        return ResponseEntity.ok(gestionService.obtenirReservationsParClient(clientId));
    }

    @GetMapping("/evenements/{evenementId}/reservations")
    public ResponseEntity<List<Reservation>> obtenirReservationsParEvenement(@PathVariable String evenementId) {
        // Ajouter vérification existence événement si nécessaire
        return ResponseEntity.ok(gestionService.obtenirReservationsParEvenement(evenementId));
    }


    // ==================== NOUVELLES ROUTES POUR L'INTEGRATION ====================

    /**
     * Endpoint pour récupérer les détails d'un événement ET les détails
     * des produits associés (via appel Feign au service Product).
     * Implémente le Scénario 1.
     */
    @GetMapping("/evenements/{eventId}/details-produits")
    public ResponseEntity<?> getEventWithProductDetails(@PathVariable String eventId) {
        logger.info("Requête GET pour /api/evenements/{}/details-produits", eventId);
        try {
            Map<String, Object> details = gestionService.getEventWithProductDetails(eventId);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des détails produits pour l'événement {}: {}", eventId, e.getMessage());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Impossible de récupérer les détails", e.getMessage(), Map.of("eventId", eventId));
        }
    }

    /**
     * Endpoint pour associer un produit existant (géré par le service Product)
     * à un événement (géré par ce service).
     * Implémente le Scénario 2.
     */
    @PostMapping("/evenements/{eventId}/products/{productId}")
    public ResponseEntity<?> addProductToEvent(@PathVariable String eventId, @PathVariable Long productId) {
        logger.info("Requête POST pour /api/evenements/{}/products/{}", eventId, productId);
        try {
            Evenement updatedEvent = gestionService.addProductToEvent(eventId, productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Produit " + productId + " associé avec succès à l'événement " + eventId);
            response.put("eventId", updatedEvent.getId());
            return ResponseEntity.ok(response);

        } catch (BusinessException e) { // Erreur métier (ex: produit non trouvé)
            logger.warn("Échec de l'association du produit {} à l'événement {}: {}", productId, eventId, e.getMessage());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Association impossible", e.getMessage(), Map.of("eventId", eventId, "productId", productId));

        } catch (RuntimeException e) { // Autres erreurs (événement non trouvé, erreur Feign...)
            logger.error("Erreur lors de l'association du produit {} à l'événement {}: {}", productId, eventId, e.getMessage());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erreur lors de l'association", e.getMessage(), Map.of("eventId", eventId, "productId", productId));
        }
    }

    // ==================== ROUTES AVANCEES EXISTANTES ====================

    @GetMapping("/evenements/recherche")
    public ResponseEntity<List<Evenement>> rechercherEvenements(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String artisteId,
            @RequestParam(required = false) Integer capaciteMin) {

        try {
            List<Evenement> resultats = gestionService.rechercherEvenementsAvances(
                    nom,
                    dateDebut != null ? Date.from(dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                    dateFin != null ? Date.from(dateFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()) : null, // dateFin est exclusive
                    artisteId,
                    capaciteMin
            );
            if (resultats.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(resultats);
        } catch (BusinessException e) {
            // Cas spécifique comme ID Artiste invalide
            return ResponseEntity.badRequest().body(null); // Ou retourner une erreur détaillée
        }
    }

    @PostMapping("/reservations/{id}/annulation")
    public ResponseEntity<?> annulerReservation(@PathVariable String id) throws BusinessException, RuntimeException {
        Reservation reservationAnnulee = gestionService.annulerReservation(id);
        return ResponseEntity.ok(reservationAnnulee);
    }

    @GetMapping("/statistiques")
    public ResponseEntity<?> obtenirStatistiques() {
        try {
            Map<String, Object> stats = gestionService.obtenirStatistiques();
            return ResponseEntity.ok(stats);
        } catch (BusinessException e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur calcul statistiques", e.getMessage());
        }
    }

    @GetMapping("/clients/{clientId}/recommandations")
    public ResponseEntity<?> obtenirRecommandations(@PathVariable String clientId, @RequestParam(required = false, defaultValue = "5") int limit) {
        try {
            List<Evenement> recommandations = gestionService.recommanderEvenements(clientId);
            if (recommandations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // Formater la réponse si besoin, ou juste retourner la liste
            Map<String, Object> response = new HashMap<>();
            response.put("clientId", clientId);
            response.put("recommandations", recommandations);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return buildErrorResponse(status, "Erreur recommandations", e.getMessage(), Map.of("clientId", clientId));
        }
    }


    // --- Méthode utilitaire pour construire les réponses d'erreur ---
    private ResponseEntity<ProblemDetail> buildErrorResponse(HttpStatus status, String title, String detail) {
        return buildErrorResponse(status, title, detail, null);
    }

    private ResponseEntity<ProblemDetail> buildErrorResponse(HttpStatus status, String title, String detail, Map<String, Object> properties) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("/errors/" + title.toLowerCase().replace(" ", "-"))); // Exemple d'URI type
        pd.setProperty("timestamp", Instant.now());
        if (properties != null) {
            properties.forEach(pd::setProperty);
        }
        logger.error("Erreur API: Status={}, Title={}, Detail={}, Properties={}", status, title, detail, properties);
        return ResponseEntity.status(status).body(pd);
    }

} // Fin de la classe GestionController