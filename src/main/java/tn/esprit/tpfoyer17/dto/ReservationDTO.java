package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Reservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Objet de transfert pour les requêtes de modification d'une réservation.
 * La date est reçue au format ISO (yyyy-MM-dd) et convertie en Date pour l'entité.
 */
public record ReservationDTO(String idReservation,
                             LocalDate anneeUniversitaire,
                             boolean estValide,
                             List<EtudiantDTO> etudiants) {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    public Reservation toEntity() {
        Set<Etudiant> etudiantsEntites = etudiants == null
                ? null
                : etudiants.stream().map(EtudiantDTO::toEntity).collect(Collectors.toSet());
        return Reservation.builder()
                .idReservation(idReservation)
                .anneeUniversitaire(anneeUniversitaire == null ? null
                        : Date.from(anneeUniversitaire.atStartOfDay(ZONE).toInstant()))
                .estValide(estValide)
                .etudiants(etudiantsEntites)
                .build();
    }
}
