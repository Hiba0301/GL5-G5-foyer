package tn.esprit.tpfoyer17.dto;

import tn.esprit.tpfoyer17.entities.Bloc;

public final class BlocMapper {

    private BlocMapper() {
        // utility class
    }

    public static BlocDTO toDTO(Bloc bloc) {
        FoyerDTO foyer = bloc.getFoyer() == null
                ? null
                : new FoyerDTO(
                        bloc.getFoyer().getIdFoyer(),
                        bloc.getFoyer().getNomFoyer(),
                        bloc.getFoyer().getCapaciteFoyer());
        return new BlocDTO(bloc.getIdBloc(), bloc.getNomBloc(), bloc.getCapaciteBloc(), foyer);
    }

    public static Bloc toEntity(BlocDTO dto) {
        return Bloc.builder()
                .idBloc(dto.idBloc())
                .nomBloc(dto.nomBloc())
                .capaciteBloc(dto.capaciteBloc())
                .foyer(dto.foyer() == null ? null : dto.foyer().toEntity())
                .build();
    }
}