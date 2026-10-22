package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Etudiant;

import java.util.Date;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'un étudiant.
 */
public record EtudiantDTO(long idEtudiant,
                          String nomEtudiant,
                          String prenomEtudiant,
                          long cinEtudiant,
                          Date dateNaissance) {

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
