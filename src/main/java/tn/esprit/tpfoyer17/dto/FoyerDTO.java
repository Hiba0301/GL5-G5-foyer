package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Foyer;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'un foyer.
 */
public record FoyerDTO(long idFoyer, String nomFoyer, long capaciteFoyer) {

    public Foyer toEntity() {
        return Foyer.builder()
                .idFoyer(idFoyer)
                .nomFoyer(nomFoyer)
                .capaciteFoyer(capaciteFoyer)
                .build();
    }
}
