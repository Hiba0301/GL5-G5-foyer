package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Etudiant;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'un étudiant.
 * La date est reçue au format ISO (yyyy-MM-dd) et convertie en Date pour l'entité.
 */
public record EtudiantDTO(long idEtudiant,
                          String nomEtudiant,
                          String prenomEtudiant,
                          long cinEtudiant,
                          LocalDate dateNaissance) {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    public Etudiant toEntity() {
        return Etudiant.builder()
                .idEtudiant(idEtudiant)
                .nomEtudiant(nomEtudiant)
                .prenomEtudiant(prenomEtudiant)
                .cinEtudiant(cinEtudiant)
                .dateNaissance(dateNaissance == null ? null
                        : Date.from(dateNaissance.atStartOfDay(ZONE).toInstant()))
                .build();
    }
}
