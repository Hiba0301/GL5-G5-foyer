package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Objet de transfert pour les requêtes de modification d'une réservation.
 */
public record ReservationDTO(String idReservation,
                             Date anneeUniversitaire,
                             boolean estValide,
                             List<EtudiantDTO> etudiants) {

    public Reservation toEntity() {
        Set<Etudiant> etudiantsEntites = etudiants == null
                ? null
                : etudiants.stream().map(EtudiantDTO::toEntity).collect(Collectors.toSet());
        return Reservation.builder()
                .idReservation(idReservation)
                .anneeUniversitaire(anneeUniversitaire)
                .estValide(estValide)
                .etudiants(etudiantsEntites)
                .build();
    }
}
