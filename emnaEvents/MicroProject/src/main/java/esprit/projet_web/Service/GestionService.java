package esprit.projet_web.Service;

import esprit.projet_web.Entity.*;
import esprit.projet_web.Exception.BusinessException;
import esprit.projet_web.Repository.*;
import esprit.projet_web.client.ProductClient; // --- IMPORT FEIGN CLIENT ---
import esprit.projet_web.dto.ProductDTO;     // --- IMPORT DTO ---
import feign.FeignException;             // --- IMPORT FEIGN EXCEPTION ---
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GestionService {

    private static final Logger logger = LoggerFactory.getLogger(GestionService.class);

    // --- Référentiels ---
    @Autowired private ArtisteRepository artisteRepository;
    @Autowired private EvenementRepository evenementRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private AdministrateurRepository administrateurRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ReservationRepositoryCustom reservationRepositoryCustom;
    @Autowired private EvenementRepositoryCustom evenementRepositoryCustom;

    // --- Autres Services/Templates ---
    @Autowired private EmailService emailService;
    @Autowired private MongoTemplate mongoTemplate;

    // --- FEIGN CLIENT INJECTION ---
    @Autowired private ProductClient productClient;

    // ==================================== ARTISTES ====================================

    public Artiste creerArtiste(Artiste artiste) {
        if (artisteRepository.existsByEmail(artiste.getEmail())) {
            throw new BusinessException("Email artiste déjà utilisé : " + artiste.getEmail());
        }
        logger.info("Création de l'artiste: {}", artiste.getEmail());
        return artisteRepository.save(artiste);
    }

    public List<Artiste> obtenirTousArtistes() {
        logger.debug("Récupération de tous les artistes");
        return artisteRepository.findAll();
    }

    public Optional<Artiste> obtenirArtisteParId(String id) {
        logger.debug("Recherche de l'artiste ID: {}", id);
        return artisteRepository.findById(id);
    }

    public Artiste mettreAJourArtiste(String id, Artiste artisteDetails) {
        Artiste artisteExistant = artisteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artiste non trouvé avec l'ID : " + id));

        // Vérifier si l'email est changé et s'il existe déjà
        if (!artisteExistant.getEmail().equals(artisteDetails.getEmail()) && artisteRepository.existsByEmail(artisteDetails.getEmail())) {
            throw new BusinessException("Le nouvel email artiste est déjà utilisé : " + artisteDetails.getEmail());
        }

        artisteExistant.setNom(artisteDetails.getNom());
        artisteExistant.setPrenom(artisteDetails.getPrenom());
        artisteExistant.setEmail(artisteDetails.getEmail());
        // Ne pas mettre à jour le mot de passe ici sans logique de hachage/sécurité
        // artisteExistant.setMotDePasse(artisteDetails.getMotDePasse());
        logger.info("Mise à jour de l'artiste ID: {}", id);
        return artisteRepository.save(artisteExistant);
    }

    public void supprimerArtiste(String id) {
        if (!artisteRepository.existsById(id)) {
            throw new RuntimeException("Artiste non trouvé avec l'ID : " + id);
        }
        // Ajouter une logique pour vérifier/gérer les événements liés avant suppression si nécessaire
        logger.warn("Suppression de l'artiste ID: {}", id);
        artisteRepository.deleteById(id);
    }

    // ==================================== EVENEMENTS ====================================

    @Transactional
    public Evenement creerEvenement(Evenement evenement, String artisteId) {
        Artiste artiste = artisteRepository.findById(artisteId)
                .orElseThrow(() -> new RuntimeException("Artiste non trouvé avec l'ID : " + artisteId + " pour l'événement " + evenement.getNom()));
        evenement.setArtiste(artiste);
        evenement.setPlacesReservees(0); // Initialiser les places réservées
        logger.info("Création de l'événement '{}' par l'artiste {}", evenement.getNom(), artisteId);
        return evenementRepository.save(evenement);
    }

    public List<Evenement> obtenirTousEvenements() {
        logger.debug("Récupération de tous les événements");
        return evenementRepository.findAll();
    }

    public List<Evenement> obtenirEvenementsAVenir() {
        logger.debug("Récupération des événements à venir");
        return evenementRepository.findByDateAfter(new Date());
    }

    public Optional<Evenement> obtenirEvenementParId(String id) {
        logger.debug("Recherche de l'événement ID: {}", id);
        return evenementRepository.findById(id);
    }

    public Evenement mettreAJourEvenement(String id, Evenement evenementDetails) {
        Evenement evenementExistant = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + id));

        evenementExistant.setNom(evenementDetails.getNom());
        evenementExistant.setDate(evenementDetails.getDate());
        evenementExistant.setDescription(evenementDetails.getDescription());
        evenementExistant.setCapaciteMax(evenementDetails.getCapaciteMax());
        evenementExistant.setTags(evenementDetails.getTags());
        // L'artiste et les places réservées ne devraient généralement pas être modifiés directement ici
        // evenementExistant.setArtiste(evenementDetails.getArtiste());
        // evenementExistant.setPlacesReservees(evenementDetails.getPlacesReservees());
        logger.info("Mise à jour de l'événement ID: {}", id);
        return evenementRepository.save(evenementExistant);
    }

    public void supprimerEvenement(String id) {
        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("Événement non trouvé avec l'ID : " + id);
        }
        // Ajouter une logique pour vérifier/gérer les réservations liées avant suppression
        logger.warn("Suppression de l'événement ID: {}", id);
        evenementRepository.deleteById(id);
    }

    public List<Evenement> obtenirEvenementsParArtiste(String artisteId) {
        logger.debug("Recherche des événements pour l'artiste ID: {}", artisteId);
        return evenementRepository.findByArtisteId(artisteId);
    }

    // ==================================== CLIENTS ====================================

    public Client creerClient(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new BusinessException("Email client déjà utilisé : " + client.getEmail());
        }
        logger.info("Création du client: {}", client.getEmail());
        return clientRepository.save(client);
    }

    public List<Client> obtenirTousClients() {
        logger.debug("Récupération de tous les clients");
        return clientRepository.findAll();
    }

    public Optional<Client> obtenirClientParId(String id) {
        logger.debug("Recherche du client ID: {}", id);
        return clientRepository.findById(id);
    }

    public Client mettreAJourClient(String id, Client clientDetails) {
        Client clientExistant = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID : " + id));

        if (!clientExistant.getEmail().equals(clientDetails.getEmail()) && clientRepository.existsByEmail(clientDetails.getEmail())) {
            throw new BusinessException("Le nouvel email client est déjà utilisé : " + clientDetails.getEmail());
        }

        clientExistant.setNom(clientDetails.getNom());
        clientExistant.setPrenom(clientDetails.getPrenom());
        clientExistant.setEmail(clientDetails.getEmail());
        // Ne pas mettre à jour le mot de passe ici sans logique de hachage/sécurité
        logger.info("Mise à jour du client ID: {}", id);
        return clientRepository.save(clientExistant);
    }

    public void supprimerClient(String id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'ID : " + id);
        }
        // Ajouter logique pour gérer les réservations liées
        logger.warn("Suppression du client ID: {}", id);
        clientRepository.deleteById(id);
    }

    // ==================================== ADMINISTRATEURS ====================================

    public Administrateur creerAdministrateur(Administrateur administrateur) {
        if (administrateurRepository.existsByEmail(administrateur.getEmail())) {
            throw new BusinessException("Email administrateur déjà utilisé : " + administrateur.getEmail());
        }
        logger.info("Création de l'administrateur: {}", administrateur.getEmail());
        return administrateurRepository.save(administrateur);
    }

    public List<Administrateur> obtenirTousAdministrateurs() {
        logger.debug("Récupération de tous les administrateurs");
        return administrateurRepository.findAll();
    }

    public Optional<Administrateur> obtenirAdministrateurParId(String id) {
        logger.debug("Recherche de l'administrateur ID: {}", id);
        return administrateurRepository.findById(id);
    }

    public Administrateur mettreAJourAdministrateur(String id, Administrateur adminDetails) {
        Administrateur adminExistant = administrateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'ID : " + id));

        if (!adminExistant.getEmail().equals(adminDetails.getEmail()) && administrateurRepository.existsByEmail(adminDetails.getEmail())) {
            throw new BusinessException("Le nouvel email administrateur est déjà utilisé : " + adminDetails.getEmail());
        }

        adminExistant.setNom(adminDetails.getNom());
        adminExistant.setPrenom(adminDetails.getPrenom());
        adminExistant.setEmail(adminDetails.getEmail());
        // Ne pas mettre à jour le mot de passe ici
        logger.info("Mise à jour de l'administrateur ID: {}", id);
        return administrateurRepository.save(adminExistant);
    }

    public void supprimerAdministrateur(String id) {
        if (!administrateurRepository.existsById(id)) {
            throw new RuntimeException("Administrateur non trouvé avec l'ID : " + id);
        }
        logger.warn("Suppression de l'administrateur ID: {}", id);
        administrateurRepository.deleteById(id);
    }

    // ==================================== RESERVATIONS ====================================

    @Transactional
    public Reservation creerReservation(Reservation reservation, String clientId, String evenementId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID : " + clientId));
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + evenementId));

        // Vérifier la capacité AVANT de sauvegarder la réservation et AVANT d'accepter implicitement
        if (evenement.getPlacesDisponibles() < reservation.getNbrPlace()) {
            throw new BusinessException("Pas assez de places disponibles pour cet événement. Places restantes: " + evenement.getPlacesDisponibles());
        }

        reservation.setClient(client);
        reservation.setEvenement(evenement);
        reservation.setDateReservation(new Date()); // S'assurer que la date est fixée
        reservation.setStatut(Reservation.StatutReservation.EN_COURS); // Statut initial

        logger.info("Création de la réservation pour le client {} sur l'événement {}", clientId, evenementId);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Notifier les admins pour la nouvelle réservation en attente
        notifyAdminsAboutReservation(savedReservation);

        // Potentiellement, appeler modifierStatutReservation si on veut accepter automatiquement
        // return modifierStatutReservation(savedReservation.getId(), Reservation.StatutReservation.ACCEPTE);
        return savedReservation;
    }

    private void notifyAdminsAboutReservation(Reservation reservation) {
        List<Administrateur> admins = administrateurRepository.findAll();
        if (admins.isEmpty()) {
            logger.warn("Aucun administrateur trouvé pour envoyer la notification de réservation.");
            return;
        }
        String subject = "Nouvelle réservation #" + reservation.getId() + " pour " + reservation.getEvenement().getNom();
        admins.forEach(admin -> {
            try {
                emailService.sendHtmlAdminNotification(admin.getEmail(), subject, reservation);
                logger.info("Notification de réservation envoyée à {}", admin.getEmail());
            } catch (Exception e) {
                logger.error("Échec de l'envoi de la notification à {}: {}", admin.getEmail(), e.getMessage());
            }
        });
    }

    public List<Reservation> obtenirToutesReservations() {
        logger.debug("Récupération de toutes les réservations");
        return reservationRepository.findAll();
    }

    public Optional<Reservation> obtenirReservationParId(String id) {
        logger.debug("Recherche de la réservation ID: {}", id);
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation mettreAJourReservation(String id, Reservation reservationDetails) {
        Reservation existante = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + id));

        // Permettre la mise à jour du nombre de places uniquement si EN_COURS ?
        if (existante.getStatut() == Reservation.StatutReservation.EN_COURS) {
            int diffPlaces = reservationDetails.getNbrPlace() - existante.getNbrPlace();
            if (diffPlaces != 0) {
                // On ne vérifie la capacité que si on ajoute des places, ou si on change le statut vers ACCEPTE plus tard
                if (diffPlaces > 0 && existante.getEvenement().getPlacesDisponibles() < diffPlaces) {
                    throw new BusinessException("Pas assez de places disponibles pour augmenter la réservation.");
                }
                // La mise à jour réelle des places réservées de l'événement se fait lors du changement de statut vers ACCEPTE
                existante.setNbrPlace(reservationDetails.getNbrPlace());
            }
        } else {
            // Si la réservation n'est plus EN_COURS, on ne peut peut-être pas changer le nombre de places
            logger.warn("Tentative de modification du nombre de places pour la réservation {} qui n'est pas EN_COURS.", id);
            if(existante.getNbrPlace() != reservationDetails.getNbrPlace()) {
                throw new BusinessException("Impossible de modifier le nombre de places pour une réservation qui n'est plus en cours.");
            }
        }
        // On pourrait permettre de changer le statut ici aussi, mais la méthode dédiée est plus propre
        // existante.setStatut(reservationDetails.getStatut());
        logger.info("Mise à jour de la réservation ID: {}", id);
        return reservationRepository.save(existante);
    }

    @Transactional
    public Reservation modifierStatutReservation(String id, Reservation.StatutReservation nouveauStatut) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + id));
        Evenement evenement = reservation.getEvenement();
        Reservation.StatutReservation ancienStatut = reservation.getStatut();

        if (ancienStatut == nouveauStatut) {
            logger.warn("Le statut de la réservation {} est déjà {}. Aucune action.", id, nouveauStatut);
            return reservation; // Pas de changement
        }

        // Logique de transition de statut et gestion de capacité
        if (nouveauStatut == Reservation.StatutReservation.ACCEPTE) {
            // On accepte une réservation (qui était EN_COURS, REFUSEE ou ANNULEE)
            if (evenement.getPlacesDisponibles() < reservation.getNbrPlace()) {
                throw new BusinessException("Impossible d'accepter: Pas assez de places disponibles (" + evenement.getPlacesDisponibles() + ") pour " + reservation.getNbrPlace() + " places demandées.");
            }
            // Incrémenter seulement si l'ancien statut n'était pas déjà ACCEPTE (ce cas est géré au début)
            if (ancienStatut != Reservation.StatutReservation.ACCEPTE) {
                evenement.incrementerPlacesReservees(reservation.getNbrPlace());
            }
        } else if (ancienStatut == Reservation.StatutReservation.ACCEPTE) {
            // On change une réservation ACCEPTÉE vers un autre statut (REFUSE, ANNULEE, EN_COURS)
            evenement.decrementerPlacesReservees(reservation.getNbrPlace());
        }

        // Mettre à jour l'événement si la capacité a changé
        if (ancienStatut == Reservation.StatutReservation.ACCEPTE || nouveauStatut == Reservation.StatutReservation.ACCEPTE) {
            evenementRepository.save(evenement);
            logger.info("Places réservées pour l'événement {} mises à jour.", evenement.getId());
        }

        // Mettre à jour le statut de la réservation
        reservation.setStatut(nouveauStatut);
        logger.info("Statut de la réservation {} changé de {} à {}", id, ancienStatut, nouveauStatut);
        return reservationRepository.save(reservation);
    }

    public void supprimerReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + id));

        // Si la réservation était acceptée, libérer les places
        if (reservation.getStatut() == Reservation.StatutReservation.ACCEPTE) {
            Evenement evenement = reservation.getEvenement();
            if (evenement != null) {
                evenement.decrementerPlacesReservees(reservation.getNbrPlace());
                evenementRepository.save(evenement);
                logger.info("Places libérées pour l'événement {} suite à la suppression de la réservation {}", evenement.getId(), id);
            }
        }
        logger.warn("Suppression de la réservation ID: {}", id);
        reservationRepository.deleteById(id);
    }

    public List<Reservation> obtenirReservationsParClient(String clientId) {
        logger.debug("Recherche des réservations pour le client ID: {}", clientId);
        return reservationRepository.findByClientId(clientId);
    }

    public List<Reservation> obtenirReservationsParEvenement(String evenementId) {
        logger.debug("Recherche des réservations pour l'événement ID: {}", evenementId);
        return reservationRepository.findByEvenementId(evenementId);
    }

    @Transactional
    public Reservation annulerReservation(String id) {
        logger.info("Tentative d'annulation de la réservation ID: {}", id);
        // Utiliser la méthode modifierStatutReservation pour la cohérence de la logique de capacité
        return modifierStatutReservation(id, Reservation.StatutReservation.ANNULEE);
    }


    // ================= METHODES AVANCEES ET UTILITAIRES =============================

    private void verifierCapaciteEvenement(String evenementId, int placesSupplementaires) {
        // Cette méthode est moins utile maintenant que la logique est dans modifierStatutReservation
        // Mais on peut la garder pour des vérifications ponctuelles si besoin.
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé lors de la vérification de capacité: " + evenementId));

        if (evenement.getPlacesDisponibles() < placesSupplementaires) {
            throw new BusinessException("Capacité maximale de l'événement atteinte ou dépassée.");
        }
    }

    public List<Evenement> rechercherEvenementsAvances(String nom, Date dateDebut, Date dateFin, String artisteId, Integer capaciteMin) {
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();

        if (nom != null && !nom.isEmpty()) {
            criteriaList.add(Criteria.where("nom").regex(nom, "i")); // Recherche insensible à la casse
        }
        if (dateDebut != null) {
            criteriaList.add(Criteria.where("date").gte(dateDebut));
        }
        if (dateFin != null) {
            // Inclure toute la journée de fin
            Calendar c = Calendar.getInstance();
            c.setTime(dateFin);
            c.add(Calendar.DATE, 1);
            criteriaList.add(Criteria.where("date").lt(c.getTime()));
        }
        if (artisteId != null && !artisteId.isEmpty()) {
            // Assurez-vous que l'ID est valide pour MongoDB si nécessaire
            try {
                criteriaList.add(Criteria.where("artiste.$id").is(new ObjectId(artisteId)));
            } catch (IllegalArgumentException e) {
                logger.error("ID Artiste invalide pour la recherche MongoDB: {}", artisteId);
                throw new BusinessException("Format de l'ID artiste invalide.");
            }
        }
        if (capaciteMin != null && capaciteMin > 0) {
            // Recherche sur capacité maximale >= capaciteMin
            criteriaList.add(Criteria.where("capaciteMax").gte(capaciteMin));
            // OU recherche sur places disponibles >= capaciteMin ? dépend du besoin
            // criteriaList.add(Criteria.where("capaciteMax - placesReservees").gte(capaciteMin)); // Moins direct en query simple
        }

        if (!criteriaList.isEmpty()) {
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);
        logger.debug("Recherche avancée d'événements avec query: {}", query);
        return mongoTemplate.find(query, Evenement.class);
    }

    @Transactional(readOnly = true) // Pas de modification de données ici
    public Map<String, Object> obtenirStatistiques() {
        logger.debug("Calcul des statistiques générales");
        Map<String, Object> stats = new HashMap<>();
        try {
            List<ReservationParStatut> reservationsParStatut = reservationRepositoryCustom.countByStatut();
            stats.put("reservationsParStatut", reservationsParStatut);

            List<EvenementPopulaire> evenementsPopulaires = evenementRepositoryCustom.findTop5ByReservationsCount();
            stats.put("evenementsPopulaires", evenementsPopulaires);

            Double tauxOccupation = evenementRepository.calculateAverageOccupationRate();
            stats.put("tauxOccupationMoyen", String.format("%.2f%%", tauxOccupation)); // Formatage

            Double chiffreAffaires = reservationRepository.calculateTotalRevenue();
            stats.put("chiffreAffairesTotal", String.format("%.2f DT", chiffreAffaires)); // Formatage

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques: {}", e.getMessage(), e);
            // Renvoyer une map vide ou lancer une exception métier ?
            throw new BusinessException("Erreur lors du calcul des statistiques: " + e.getMessage());
        }
        return stats;
    }

    @Transactional(readOnly = true) // Pas de modification ici
    public List<Evenement> recommanderEvenements(String clientId) {
        logger.debug("Génération de recommandations pour le client ID: {}", clientId);
        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Client non trouvé pour les recommandations : " + clientId);
        }

        List<Reservation> historique = reservationRepository.findByClientId(clientId);
        Date maintenant = new Date();
        int limit = 5; // Limite de recommandations

        // Cas 1: Pas d'historique -> Prochains événements généraux
        if (historique.isEmpty()) {
            logger.debug("Aucun historique pour client {}, recommandation des prochains événements généraux.", clientId);
            Page<Evenement> nextPage = evenementRepository.findByDateAfter(
                    maintenant,
                    PageRequest.of(0, limit, Sort.by("date").ascending())
            );
            return nextPage.getContent();
        }

        // Cas 2: Analyse de l'historique
        // Obtenir les IDs des événements déjà réservés par le client
        Set<String> evenementsReservesIds = historique.stream()
                .map(r -> r.getEvenement().getId())
                .collect(Collectors.toSet());

        // Compter les artistes préférés
        Map<String, Long> artisteCounts = historique.stream()
                .filter(r -> r.getEvenement() != null && r.getEvenement().getArtiste() != null) // Vérifier nullité
                .map(r -> r.getEvenement().getArtiste().getId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Ordonner les artistes
        List<String> artistesTries = artisteCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        logger.debug("Artistes préférés pour client {}: {}", clientId, artistesTries);

        // Chercher les événements futurs des artistes préférés, non déjà réservés
        List<Evenement> recommandations = new ArrayList<>();
        Set<String> recommandationsIds = new HashSet<>(); // Pour éviter doublons

        for (String artisteId : artistesTries) {
            if (recommandations.size() >= limit) break;
            Page<Evenement> eventsPage = evenementRepository.findByArtisteIdAndDateAfter(
                    artisteId,
                    maintenant,
                    PageRequest.of(0, limit - recommandations.size()) // Demander juste le complément
            );
            for(Evenement event : eventsPage.getContent()){
                if(!evenementsReservesIds.contains(event.getId()) && recommandationsIds.add(event.getId())){
                    recommandations.add(event);
                    if (recommandations.size() >= limit) break;
                }
            }
        }

        // Compléter avec d'autres événements futurs non réservés si nécessaire
        if (recommandations.size() < limit) {
            logger.debug("Complément des recommandations avec événements généraux pour client {}", clientId);
            Page<Evenement> autresEventsPage = evenementRepository.findByDateAfter(
                    maintenant,
                    PageRequest.of(0, limit) // Demander un peu plus au cas où certains sont déjà dedans
            );
            for(Evenement event : autresEventsPage.getContent()){
                if (recommandations.size() >= limit) break;
                if(!evenementsReservesIds.contains(event.getId()) && recommandationsIds.add(event.getId())){
                    recommandations.add(event);
                }
            }
        }
        logger.info("Retour de {} recommandations pour le client {}", recommandations.size(), clientId);
        return recommandations.subList(0, Math.min(recommandations.size(), limit)); // Assurer la limite
    }


    // --- NOUVELLES METHODES UTILISANT FEIGN ---

    /**
     * Récupère un événement et les détails des produits associés depuis le service Product.
     * Implémente le Scénario 1.
     *
     * @param eventId L'ID MongoDB de l'événement.
     * @return Une Map contenant l'objet Evenement et la liste des ProductDTO associés.
     * @throws RuntimeException si l'événement n'est pas trouvé.
     */
    public Map<String, Object> getEventWithProductDetails(String eventId) {
        logger.debug("Récupération des détails produits pour l'événement ID: {}", eventId);
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + eventId));

        List<ProductDTO> productDetails = new ArrayList<>();
        Set<Long> productIds = evenement.getProductIds(); // Utilise le getter qui initialise si besoin

        if (productIds != null && !productIds.isEmpty()) {
            logger.info("Événement {} a {} produit(s) associé(s): {}", eventId, productIds.size(), productIds);

            // --- Méthode 1: Appels individuels ---
            for (Long productId : productIds) {
                try {
                    logger.debug("Appel Feign à Product Service pour l'ID produit: {}", productId);
                    ProductDTO dto = productClient.getProductById(productId);
                    if (dto != null) {
                        productDetails.add(dto);
                        logger.debug("Détails du produit {} récupérés avec succès.", productId);
                    } else {
                        logger.warn("L'appel Feign pour le produit {} a retourné null.", productId);
                    }
                } catch (FeignException.NotFound e) {
                    logger.warn("Produit avec ID {} référencé dans l'événement {} non trouvé dans Product Service (404).", productId, eventId);
                } catch (FeignException e) {
                    logger.error("Erreur Feign ({}) en appelant Product Service pour ID {}: {}", e.status(), productId, e.getMessage());
                } catch (Exception e) {
                    logger.error("Erreur inattendue lors de la récupération du produit ID {}: {}", productId, e.getMessage(), e);
                }
            }

            // --- Méthode 2: Appel par lot (si implémentée) ---
            /*
            try {
                logger.debug("Appel Feign par lot à Product Service pour les IDs: {}", productIds);
                // Assurez-vous que la méthode existe dans ProductClient et ProductController
                // productDetails = productClient.getProductsByIds(new ArrayList<>(productIds));
                logger.info("Détails récupérés pour {} produits via appel par lot.", productDetails.size());
            } catch (FeignException e) {
                 logger.error("Erreur Feign ({}) lors de l'appel par lot à Product Service pour IDs {}: {}", e.status(), productIds, e.getMessage());
                 productDetails = Collections.emptyList();
            } catch (Exception e) {
                 logger.error("Erreur inattendue lors de l'appel par lot pour produits IDs {}: {}", productIds, e.getMessage(), e);
                 productDetails = Collections.emptyList();
            }
            */

        } else {
            logger.info("L'événement {} n'a pas de produits associés.", eventId);
        }

        // Préparation de la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("event", evenement);
        response.put("products", productDetails);

        logger.debug("Retour des détails pour l'événement {} avec {} produits.", eventId, productDetails.size());
        return response;
    }

    /**
     * Ajoute une référence de produit à un événement existant.
     * Vérifie d'abord l'existence du produit via le service Product.
     * Implémente le Scénario 2.
     *
     * @param eventId L'ID MongoDB de l'événement.
     * @param productId L'ID (Long) du produit à ajouter (venant du service Product).
     * @return L'objet Evenement mis à jour et sauvegardé.
     * @throws RuntimeException si l'événement n'est pas trouvé.
     * @throws BusinessException si le produit n'est pas trouvé dans le service Product (404).
     * @throws RuntimeException pour d'autres erreurs de communication Feign ou inattendues.
     */
    @Transactional
    public Evenement addProductToEvent(String eventId, Long productId) {
        logger.info("Tentative d'ajout du produit ID {} à l'événement ID {}", productId, eventId);

        // 1. Vérifier si le produit existe via Feign
        try {
            logger.debug("Vérification de l'existence du produit ID {} via Feign...", productId);
            ProductDTO product = productClient.getProductById(productId);
            if (product == null) {
                logger.error("Product Service a retourné une réponse nulle pour le produit ID {}. Annulation.", productId);
                throw new RuntimeException("Réponse invalide du service produits pour l'ID " + productId);
            }
            logger.debug("Produit ID {} trouvé dans Product Service.", productId);
        } catch (FeignException.NotFound e) {
            logger.warn("Produit ID {} non trouvé dans Product Service (404).", productId);
            throw new BusinessException("Association impossible : Le produit avec l'ID " + productId + " n'existe pas.");
        } catch (FeignException e) {
            logger.error("Erreur Feign ({}) lors de la vérification du produit ID {}: {}", e.status(), productId, e.getMessage());
            throw new RuntimeException("Erreur de communication avec le service produits lors de la vérification de l'ID " + productId + ".", e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la vérification du produit ID {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Erreur inattendue lors de la vérification du produit " + productId + ".", e);
        }

        // 2. Récupérer l'événement local
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Événement ID {} non trouvé.", eventId);
                    return new RuntimeException("Événement non trouvé avec l'ID : " + eventId);
                });

        // 3. Ajouter l'ID du produit au Set (évite les doublons)
        boolean added = evenement.getProductIds().add(productId); // Utilise le getter pour l'initialisation

        if (added) {
            // 4. Sauvegarder l'événement mis à jour
            logger.info("Produit ID {} ajouté avec succès à l'événement ID {}. Sauvegarde...", productId, eventId);
            return evenementRepository.save(evenement);
        } else {
            logger.warn("Le produit ID {} est déjà associé à l'événement ID {}. Aucune modification.", productId, eventId);
            return evenement;
        }
    }

} // Fin de la classe GestionService