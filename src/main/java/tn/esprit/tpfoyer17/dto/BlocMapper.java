package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Bloc;

public final class BlocMapper {

    private BlocMapper() {
        // utility class
    }

    public static BlocDTO toDTO(Bloc bloc) {
        return new BlocDTO(bloc.getIdBloc(), bloc.getNomBloc(), bloc.getCapaciteBloc());
    }

    public static Bloc toEntity(BlocDTO dto) {
        return Bloc.builder()
                .idBloc(dto.idBloc())
                .nomBloc(dto.nomBloc())
                .capaciteBloc(dto.capaciteBloc())
                .build();
    }
}