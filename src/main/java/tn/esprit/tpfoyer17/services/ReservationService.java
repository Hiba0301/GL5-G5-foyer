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

        Reservation existante = reservationRepository.findForReservation(idBloc);

        if (existante != null) {
            log.debug("Réservation existante trouvée pour le bloc {} : {}", idBloc, existante.getIdReservation());
            existante.getEtudiants().add(etudiantRepository.findByCinEtudiant(cinEtudiant));
            Chambre chambre = chambreRepository.findByReservationsIdReservation(existante.getIdReservation());
            switch (chambre.getTypeChambre()) {
                case TRIPLE:
                    if (existante.getEtudiants().size() == 3) {
                        existante.setEstValide(false);
                        log.info("Réservation {} complète (chambre TRIPLE, 3 étudiants)", existante.getIdReservation());
                    }
                    break;
                case DOUBLE:
                    existante.setEstValide(false);
                    log.info("Réservation {} complète (chambre DOUBLE)", existante.getIdReservation());
                    break;
            }
            Reservation saved = reservationRepository.save(existante);
            log.info("Étudiant (cin {}) ajouté à la réservation {}", cinEtudiant, saved.getIdReservation());
            return saved;
        } else {
            log.debug("Aucune réservation existante pour le bloc {}, création d'une nouvelle réservation", idBloc);
            List<Etudiant> etudiants = new ArrayList<Etudiant>();
            etudiants.add(etudiantRepository.findByCinEtudiant(cinEtudiant));
            Reservation reservation = Reservation.builder()
                    .anneeUniversitaire(LocalDate.now(ZONE))
                    .etudiants((Set<Etudiant>) etudiants)
                    .build();
            Chambre chambre = chambreRepository.getForReservation(idBloc);
            Bloc bloc = blocRepository.findById(idBloc).orElseThrow(() -> {
                log.error("Bloc introuvable avec l'id {}", idBloc);
                return new NoSuchElementException("Bloc introuvable avec l'id " + idBloc);
            });
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String idReservation = chambre.getIdChambre() + bloc.getNomBloc() + calendar.get(Calendar.YEAR);
            reservation.setIdReservation(idReservation);
            if (chambre.getTypeChambre().equals(TypeChambre.SIMPLE)) {
                reservation.setEstValide(false);
            } else {
                reservation.setEstValide(true);
            }
            log.debug("Nouvelle réservation {} (chambre {}, valide : {})",
                    idReservation, chambre.getTypeChambre(), reservation.isEstValide());

            Reservation saved = reservationRepository.save(reservation);
            log.info("Nouvelle réservation {} créée pour l'étudiant (cin {})", saved.getIdReservation(), cinEtudiant);
            return saved;
        }
    }

    @Override
    public Reservation annulerReservation(long cinEtudiant) {
        log.info("Annulation de la réservation de l'étudiant (cin {})", cinEtudiant);
        int annee = Year.now(ZONE).getValue();
        log.debug("Recherche de la réservation de l'étudiant (cin {}) pour l'année {}", cinEtudiant, annee);

        Reservation reservation = reservationRepository.findByEtudiantsCinEtudiantAndAnneeUniversitaire(cinEtudiant, annee);
        if (reservation == null) {
            log.warn("Aucune réservation trouvée pour l'étudiant (cin {}) en {}", cinEtudiant, annee);
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

        List<Reservation> resultats = reservationRepository.findByAnneeUniversitaire_YearAndNomUnuiversite(annee, nomUniversite);
        log.info("{} réservation(s) trouvée(s) pour l'université {}", resultats.size(), nomUniversite);
        return resultats;
    }
}