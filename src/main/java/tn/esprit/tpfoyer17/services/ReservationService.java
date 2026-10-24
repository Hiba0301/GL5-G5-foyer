package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;
import tn.esprit.tpfoyer17.repositories.ReservationRepository;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.time.ZoneId;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationService implements IReservationService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    ReservationRepository reservationRepository;
    EtudiantRepository etudiantRepository;
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;

    @Override
    public List<Reservation> getAllReservations() {
        log.info("Récupération de toutes les réservations");
        List<Reservation> reservations = (List<Reservation>) reservationRepository.findAll();
        log.debug("Nombre de réservations récupérées : {}", reservations.size());
        return reservations;
    }

    @Override
    public Reservation getReservationById(String idReservation) {
        log.debug("Recherche de la réservation avec l'id {}", idReservation);
        return reservationRepository.findById(idReservation).orElseThrow(() -> {
            log.error("Réservation introuvable avec l'id {}", idReservation);
            return new NoSuchElementException("Réservation introuvable avec l'id " + idReservation);
        });
    }

    @Override
    public void deleteReservation(String idReservation) {
        if (!reservationRepository.existsById(idReservation)) {
            log.warn("Suppression demandée pour une réservation inexistante, id {}", idReservation);
        }
        reservationRepository.deleteById(idReservation);
        log.info("Suppression de la réservation avec l'id {}", idReservation);
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        log.info("Mise à jour de la réservation avec l'id {}", reservation.getIdReservation());
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation ajouterReservation(long idBloc, long cinEtudiant) {
        log.info("Ajout d'un étudiant (cin {}) à une réservation du bloc {}", cinEtudiant, idBloc);
        Etudiant etudiant = etudiantRepository.findByCinEtudiant(cinEtudiant);
        Reservation existante = reservationRepository.findForReservation(idBloc);

        if (existante != null) {
            log.debug("Réservation existante trouvée pour le bloc {} : {}", idBloc, existante.getIdReservation());
            existante.getEtudiants().add(etudiant);
            Chambre chambre = chambreRepository.findByReservationsIdReservation(existante.getIdReservation());
            TypeChambre type = chambre.getTypeChambre();
            // SIMPLE et DOUBLE sont complètes avec ce nouvel étudiant, TRIPLE à partir de 3
            if (type != TypeChambre.TRIPLE || existante.getEtudiants().size() == 3) {
                existante.setEstValide(false);
                log.info("Réservation {} complète (chambre {})", existante.getIdReservation(), type);
            }
            Reservation saved = reservationRepository.save(existante);
            log.info("Étudiant (cin {}) ajouté à la réservation {}", cinEtudiant, saved.getIdReservation());
            return saved;
        }

        log.debug("Aucune réservation existante pour le bloc {}, création d'une nouvelle réservation", idBloc);
        Set<Etudiant> etudiants = new HashSet<>();
        etudiants.add(etudiant);
        Chambre chambre = chambreRepository.getForReservation(idBloc);
        Bloc bloc = blocRepository.findById(idBloc).orElseThrow(() -> {
            log.error("Bloc introuvable avec l'id {}", idBloc);
            return new NoSuchElementException("Bloc introuvable avec l'id " + idBloc);
        });
        String idReservation = chambre.getIdChambre() + bloc.getNomBloc() + Year.now(ZONE).getValue();
        Reservation reservation = Reservation.builder()
                .idReservation(idReservation)
                .anneeUniversitaire(LocalDate.now(ZONE))
                .etudiants(etudiants)
                .estValide(chambre.getTypeChambre() != TypeChambre.SIMPLE)
                .build();
        log.debug("Nouvelle réservation {} (chambre {}, valide : {})",
                idReservation, chambre.getTypeChambre(), reservation.isEstValide());

        Reservation saved = reservationRepository.save(reservation);
        log.info("Nouvelle réservation {} créée pour l'étudiant (cin {})", saved.getIdReservation(), cinEtudiant);
        return saved;
    }

    @Override
    public Reservation annulerReservation(long cinEtudiant) {
        log.info("Annulation de la réservation de l'étudiant (cin {})", cinEtudiant);
        int annee = Year.now(ZONE).getValue();
        log.debug("Recherche de la réservation de l'étudiant (cin {}) pour l'année {}", cinEtudiant, annee);

        Reservation reservation = reservationRepository.findByEtudiantsCinEtudiantAndAnneeUniversitaire(cinEtudiant, annee);
        if (reservation == null) {
            log.warn("Aucune réservation trouvée pour l'étudiant (cin {}) en {}", cinEtudiant, annee);
            throw new NoSuchElementException("Aucune réservation pour l'étudiant (cin " + cinEtudiant + ") en " + annee);
        }

        Etudiant etudiant = etudiantRepository.findByCinEtudiant(cinEtudiant);
        reservation.getEtudiants().remove(etudiant);
        reservation.setEstValide(true);
        Reservation saved = reservationRepository.save(reservation);
        log.info("Étudiant (cin {}) retiré de la réservation {}", cinEtudiant, saved.getIdReservation());
        return saved;
    }

    @Override
    public List<Reservation> getReservationParAnneeUniversitaireEtNomUniversite(Date anneeUniversite, String nomUniversite) {
        log.info("Recherche des réservations pour l'université {} et la date {}", nomUniversite, anneeUniversite);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(anneeUniversite);
        int annee = calendar.get(Calendar.YEAR);
        log.debug("Année extraite : {}", annee);

        List<Reservation> resultats = reservationRepository.findByAnneeUniversitaireYearAndNomUniversite(annee, nomUniversite);
        log.info("{} réservation(s) trouvée(s) pour l'université {}", resultats.size(), nomUniversite);
        return resultats;
    }
}