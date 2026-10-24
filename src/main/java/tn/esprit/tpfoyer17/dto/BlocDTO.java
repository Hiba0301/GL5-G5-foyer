package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Bloc;

public record BlocDTO(long idBloc, String nomBloc, long capaciteBloc, FoyerDTO foyer) {

    public BlocDTO(long idBloc, String nomBloc, long capaciteBloc) {
        this(idBloc, nomBloc, capaciteBloc, null);
    }

    public Bloc toEntity() {
        return Bloc.builder()
                .idBloc(idBloc)
                .nomBloc(nomBloc)
                .capaciteBloc(capaciteBloc)
                .foyer(foyer == null ? null : foyer.toEntity())
                .build();
    }
}