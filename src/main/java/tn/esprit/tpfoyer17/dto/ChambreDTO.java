package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'une chambre.
 */
public record ChambreDTO(long idChambre, long numeroChambre, TypeChambre typeChambre, long idBloc) {

    public Chambre toEntity() {
        Bloc bloc = idBloc > 0 ? Bloc.builder().idBloc(idBloc).build() : null;
        return Chambre.builder()
                .idChambre(idChambre)
                .numeroChambre(numeroChambre)
                .typeChambre(typeChambre)
                .bloc(bloc)
                .build();
    }
}