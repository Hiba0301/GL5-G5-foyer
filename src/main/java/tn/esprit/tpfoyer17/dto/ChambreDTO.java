package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'une chambre.
 */
public record ChambreDTO(long idChambre, long numeroChambre, TypeChambre typeChambre) {

    public Chambre toEntity() {
        return Chambre.builder()
                .idChambre(idChambre)
                .numeroChambre(numeroChambre)
                .typeChambre(typeChambre)
                .build();
    }
}
