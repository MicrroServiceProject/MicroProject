package esprit.projet_web.Service;

import esprit.projet_web.Entity.*;
import esprit.projet_web.Exception.BusinessException;
import esprit.projet_web.Repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GestionService {

    @Autowired private ArtisteRepository artisteRepository;
    @Autowired private EvenementRepository evenementRepository;
    @Autowired private ClientRepository clientRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired
    private ReservationRepositoryCustom reservationRepositoryCustom; // Doit être dans le package Repository

    @Autowired
    private EvenementRepositoryCustom evenementRepositoryCustom; // Doit être dans le package Repository
    @Autowired
    private MongoTemplate mongoTemplate;
    // ==================================== ARTISTES ====================================
    public Artiste creerArtiste(Artiste artiste) {
        if (artisteRepository.existsByEmail(artiste.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return artisteRepository.save(artiste);
    }

    public List<Artiste> obtenirTousArtistes() {
        return artisteRepository.findAll();
    }

    public Optional<Artiste> obtenirArtisteParId(String id) {
        return artisteRepository.findById(id);
    }

    public Artiste mettreAJourArtiste(String id, Artiste artiste) {
        Artiste existant = artisteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artiste non trouvé"));
        artiste.setId(id);
        return artisteRepository.save(artiste);
    }

    public void supprimerArtiste(String id) {
        artisteRepository.deleteById(id);
    }

    // ==================================== EVENEMENTS ====================================
    @Transactional
    public Evenement creerEvenement(Evenement evenement, String artisteId) {
        Artiste artiste = artisteRepository.findById(artisteId)
                .orElseThrow(() -> new RuntimeException("Artiste non trouvé"));
        evenement.setArtiste(artiste);
        return evenementRepository.save(evenement);
    }

    public List<Evenement> obtenirTousEvenements() {
        return evenementRepository.findAll();
    }

    public List<Evenement> obtenirEvenementsAVenir() {
        return evenementRepository.findByDateAfter(new Date());
    }

    public Optional<Evenement> obtenirEvenementParId(String id) {
        return evenementRepository.findById(id);
    }

    public Evenement mettreAJourEvenement(String id, Evenement evenement) {
        Evenement existant = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        evenement.setId(id);
        return evenementRepository.save(evenement);
    }

    public void supprimerEvenement(String id) {
        evenementRepository.deleteById(id);
    }

    public List<Evenement> obtenirEvenementsParArtiste(String artisteId) {
        return evenementRepository.findByArtisteId(artisteId);
    }

    // ==================================== CLIENTS ====================================
    public Client creerClient(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return clientRepository.save(client);
    }

    public List<Client> obtenirTousClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> obtenirClientParId(String id) {
        return clientRepository.findById(id);
    }

    public Client mettreAJourClient(String id, Client client) {
        Client existant = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        client.setId(id);
        return clientRepository.save(client);
    }

    public void supprimerClient(String id) {
        clientRepository.deleteById(id);
    }

    // ==================================== ADMINISTRATEURS ====================================
    public Administrateur creerAdministrateur(Administrateur administrateur) {
        if (administrateurRepository.existsByEmail(administrateur.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return administrateurRepository.save(administrateur);
    }

    public List<Administrateur> obtenirTousAdministrateurs() {
        return administrateurRepository.findAll();
    }

    public Optional<Administrateur> obtenirAdministrateurParId(String id) {
        return administrateurRepository.findById(id);
    }

    public Administrateur mettreAJourAdministrateur(String id, Administrateur administrateur) {
        Administrateur existant = administrateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
        administrateur.setId(id);
        return administrateurRepository.save(administrateur);
    }

    public void supprimerAdministrateur(String id) {
        administrateurRepository.deleteById(id);
    }

    // ==================================== RESERVATIONS ====================================
    @Transactional
    public Reservation creerReservation(Reservation reservation, String clientId, String evenementId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Vérifier et mettre à jour les places
        evenement.incrementerPlacesReservees(reservation.getNbrPlace());
        evenementRepository.save(evenement);

        reservation.setClient(client);
        reservation.setEvenement(evenement);

        Reservation savedReservation = reservationRepository.save(reservation);

        // Envoyer notification aux administrateurs
        notifyAdminsAboutReservation(savedReservation);

        return savedReservation;
    }

    private void notifyAdminsAboutReservation(Reservation reservation) {
        List<Administrateur> admins = administrateurRepository.findAll();

        String subject = "Nouvelle réservation #" + reservation.getId();
        String text = String.format(
                "Une nouvelle réservation a été effectuée:\n" +
                        "Client: %s %s (%s)\n" +
                        "Événement: %s\n" +
                        "Places: %d\n" +
                        "Date: %s",
                reservation.getClient().getPrenom(),
                reservation.getClient().getNom(),
                reservation.getClient().getEmail(),
                reservation.getEvenement().getNom(),
                reservation.getNbrPlace(),
                reservation.getDateReservation()
        );

        admins.forEach(admin -> {
            emailService.sendAdminNotification(
                    admin.getEmail(),
                    subject,
                    text
            );
        });
    }

    public List<Reservation> obtenirToutesReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> obtenirReservationParId(String id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation mettreAJourReservation(String id, Reservation reservation) {
        Reservation existante = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getNbrPlace() != 0) {
            verifierCapaciteEvenement(
                    existante.getEvenement().getId(),
                    reservation.getNbrPlace() - existante.getNbrPlace()
            );
        }

        reservation.setId(id);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation modifierStatutReservation(String id, Reservation.StatutReservation statut) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (statut == Reservation.StatutReservation.ACCEPTE) {
            verifierCapaciteEvenement(
                    reservation.getEvenement().getId(),
                    reservation.getNbrPlace()
            );
        }

        reservation.setStatut(statut);
        return reservationRepository.save(reservation);
    }

    public void supprimerReservation(String id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> obtenirReservationsParClient(String clientId) {
        return reservationRepository.findByClientId(clientId);
    }

    public List<Reservation> obtenirReservationsParEvenement(String evenementId) {
        return reservationRepository.findByEvenementId(evenementId);
    }

    // ==================================== METHODES UTILITAIRES ====================================
    private void verifierCapaciteEvenement(String evenementId, int placesSupplementaires) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        long reservationsAcceptees = reservationRepository.countByEvenementIdAndStatut(
                evenementId, Reservation.StatutReservation.ACCEPTE);

        if (reservationsAcceptees + placesSupplementaires > evenement.getCapaciteMax()) {
            throw new RuntimeException("Capacité maximale de l'événement atteinte");
        }
    }
    //methode avancé
    // Dans GestionService.java
    public List<Evenement> rechercherEvenementsAvances(
            String nom,
            Date dateDebut,
            Date dateFin,
            String artisteId,
            Integer capaciteMin) {

        Criteria criteria = new Criteria();

        if (nom != null && !nom.isEmpty()) {
            criteria.and("nom").regex(nom, "i");
        }

        if (dateDebut != null && dateFin != null) {
            criteria.and("date").gte(dateDebut).lte(dateFin);
        } else if (dateDebut != null) {
            criteria.and("date").gte(dateDebut);
        } else if (dateFin != null) {
            criteria.and("date").lte(dateFin);
        }

        if (artisteId != null && !artisteId.isEmpty()) {
            criteria.and("artiste.$id").is(new ObjectId(artisteId));
        }

        if (capaciteMin != null) {
            criteria.and("capaciteMax").gte(capaciteMin);
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Evenement.class);
    }
    @Transactional
    public Reservation annulerReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatut() != Reservation.StatutReservation.ACCEPTE) {
            throw new BusinessException("Seules les réservations acceptées peuvent être annulées");
        }

        Evenement evenement = reservation.getEvenement();
        if (reservation.getNbrPlace() <= 0) {  // Modifié ici (<= 0 au lieu de < 0)
            throw new BusinessException("Nombre de places réservées invalide");
        }

        evenement.decrementerPlacesReservees(reservation.getNbrPlace());
        evenementRepository.save(evenement);

        reservation.setStatut(Reservation.StatutReservation.ANNULEE);
        return reservationRepository.save(reservation);
    }
    @Transactional
    public Map<String, Object> obtenirStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Réservations par statut
            List<ReservationParStatut> reservationsParStatut = reservationRepositoryCustom.countByStatut();
            stats.put("reservationsParStatut", reservationsParStatut);

            // Événements les plus populaires
            List<EvenementPopulaire> evenementsPopulaires = evenementRepositoryCustom.findTop5ByReservationsCount();
            stats.put("evenementsPopulaires", evenementsPopulaires);

            // Taux d'occupation moyen (en pourcentage)
            Double tauxOccupation = evenementRepository.calculateAverageOccupationRate();
            stats.put("tauxOccupationMoyen", String.format("%.2f%%", tauxOccupation));

            // Chiffre d'affaires total
            Double chiffreAffaires = reservationRepository.calculateTotalRevenue();
            stats.put("chiffreAffairesTotal", String.format("%.2fDT", chiffreAffaires));

        } catch (Exception e) {
            throw new BusinessException("Erreur lors du calcul des statistiques: " + e.getMessage());
        }

        return stats;
    }
    @Transactional
    public List<Evenement> recommanderEvenements(String clientId) {
        // 1. Obtenir l'historique des réservations du client
        List<Reservation> historique = reservationRepository.findByClientId(clientId);

        // 2. Si pas d'historique, retourner les prochains événements (paginés)
        if (historique.isEmpty()) {
            return evenementRepository.findByDateAfter(
                    new Date(),
                    PageRequest.of(0, 5, Sort.by("date").ascending())
            ).getContent();
        }

        // 3. Analyser l'historique pour trouver les préférences
        Map<String, Long> artisteCounts = historique.stream()
                .map(r -> r.getEvenement().getArtiste().getId())
                .collect(Collectors.groupingBy(
                        artisteId -> artisteId,
                        Collectors.counting()
                ));

        // 4. Ordonner les artistes par préférence
        List<String> artistesTries = artisteCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 5. Chercher des événements des artistes préférés (paginés)
        List<Evenement> recommandations = new ArrayList<>();
        for (String artisteId : artistesTries) {
            Page<Evenement> eventsPage = evenementRepository.findByArtisteIdAndDateAfter(
                    artisteId,
                    new Date(),
                    PageRequest.of(0, 5 - recommandations.size())
            );
            recommandations.addAll(eventsPage.getContent());
            if (recommandations.size() >= 5) break;
        }

        // 6. Compléter si nécessaire avec d'autres événements (paginés)
        if (recommandations.size() < 5) {
            Page<Evenement> autresEvents = evenementRepository.findByDateAfter(
                    new Date(),
                    PageRequest.of(0, 5 - recommandations.size())
            );
            recommandations.addAll(autresEvents.getContent());
        }

        return recommandations;
    }
}