package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Etudiant;

import java.time.LocalDate;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'un étudiant.
 * La date de naissance est reçue au format ISO (yyyy-MM-dd).
 */
public record EtudiantDTO(long idEtudiant,
                          String nomEtudiant,
                          String prenomEtudiant,
                          long cinEtudiant,
                          LocalDate dateNaissance) {

    public Etudiant toEntity() {
        return Etudiant.builder()
                .idEtudiant(idEtudiant)
                .nomEtudiant(nomEtudiant)
                .prenomEtudiant(prenomEtudiant)
                .cinEtudiant(cinEtudiant)
                .dateNaissance(dateNaissance)
                .build();
    }
}
