package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Bloc;

/**
 * Objet de transfert pour les requêtes d'ajout / modification d'un bloc.
 * Les chambres d'un bloc sont gérées via l'endpoint affecter-chambre-bloc.
 */
public record BlocDTO(long idBloc, String nomBloc, long capaciteBloc, FoyerDTO foyer) {

    public Bloc toEntity() {
        return Bloc.builder()
                .idBloc(idBloc)
                .nomBloc(nomBloc)
                .capaciteBloc(capaciteBloc)
                .foyer(foyer == null ? null : foyer.toEntity())
                .build();
    }
}
