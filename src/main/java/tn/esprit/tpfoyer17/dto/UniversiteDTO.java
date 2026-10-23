package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Universite;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'une université.
 */
public record UniversiteDTO(long idUniversite, String nomUniversite, String adresse, FoyerDTO foyer) {

    public Universite toEntity() {
        return Universite.builder()
                .idUniversite(idUniversite)
                .nomUniversite(nomUniversite)
                .adresse(adresse)
                .foyer(foyer == null ? null : foyer.toEntity())
                .build();
    }
}
