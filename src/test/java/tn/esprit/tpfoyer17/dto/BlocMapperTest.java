package tn.esprit.tpfoyer17.dto;

import org.junit.jupiter.api.Test;
import tn.esprit.tpfoyer17.entities.Bloc;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlocMapperTest {

    @Test
    void toDTOAndToEntity() {
        Bloc bloc = Bloc.builder().idBloc(1L).nomBloc("Bloc A").capaciteBloc(50).build();

        BlocDTO dto = BlocMapper.toDTO(bloc);
        assertEquals(1L, dto.idBloc());
        assertEquals("Bloc A", dto.nomBloc());
        assertEquals(50, dto.capaciteBloc());

        Bloc back = BlocMapper.toEntity(dto);
        assertEquals(1L, back.getIdBloc());
        assertEquals("Bloc A", back.getNomBloc());
        assertEquals(50, back.getCapaciteBloc());
    }
}