package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.BlocDTO;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.services.IBlocService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlocControllerTest {

    @Mock
    IBlocService blocService;

    @InjectMocks
    BlocController controller;

    private Bloc bloc() {
        return Bloc.builder().idBloc(1L).nomBloc("Bloc A").capaciteBloc(50).build();
    }

    @Test
    void addingBloc() {
        when(blocService.addBloc(any(Bloc.class))).thenReturn(bloc());
        BlocDTO result = controller.addingBloc(new BlocDTO(0, "Bloc A", 50));
        assertEquals("Bloc A", result.nomBloc());
    }

    @Test
    void gettingAllBloc() {
        when(blocService.getAllBlocs()).thenReturn(List.of(bloc()));
        assertEquals(1, controller.gettingAllBloc().size());
    }

    @Test
    void gettingBloc() {
        when(blocService.getBlocById(1L)).thenReturn(bloc());
        assertEquals(1L, controller.gettingBloc(1L).idBloc());
    }

    @Test
    void deletingBloc() {
        controller.deletingBloc(1L);
        verify(blocService).deleteBloc(1L);
    }

    @Test
    void updatingBloc() {
        when(blocService.updateBloc(any(Bloc.class))).thenReturn(bloc());
        assertEquals(50, controller.updatingBloc(new BlocDTO(1, "Bloc A", 50)).capaciteBloc());
    }

    @Test
    void affecterChambresABloc() {
        when(blocService.affecterChambresABloc(List.of(1L, 2L), 1L)).thenReturn(bloc());
        assertEquals(1L, controller.affecterChambresABloc(List.of(1L, 2L), 1L).idBloc());
    }
}