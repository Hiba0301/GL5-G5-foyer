package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.ChambreDTO;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.services.IChambreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChambreControllerTest {

    @Mock
    IChambreService chambreService;

    @InjectMocks
    ChambreController controller;

    private Chambre chambre() {
        return Chambre.builder().idChambre(1L).numeroChambre(101L).typeChambre(TypeChambre.SIMPLE).build();
    }

    @Test
    void addingChambre() {
        when(chambreService.addChambre(any(Chambre.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(101L, controller.addingChambre(new ChambreDTO(0L, 101L, TypeChambre.SIMPLE)).getNumeroChambre());
    }

    @Test
    void gettingAllChambre() {
        when(chambreService.getAllChambres()).thenReturn(List.of(chambre()));

        assertEquals(1, controller.gettingAllChambre().size());
    }

    @Test
    void gettingChambre() {
        when(chambreService.getChambreById(1L)).thenReturn(chambre());

        assertEquals(101L, controller.gettingChambre(1L).getNumeroChambre());
    }

    @Test
    void deletingChambre() {
        controller.deletingChambre(1L);

        verify(chambreService).deleteChambre(1L);
    }

    @Test
    void updatingChambre() {
        when(chambreService.updateChambre(any(Chambre.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(TypeChambre.DOUBLE, controller.updatingChambre(new ChambreDTO(1L, 101L, TypeChambre.DOUBLE)).getTypeChambre());
    }

    @Test
    void getChambresParNomUniversite() {
        List<Chambre> chambres = List.of(chambre());
        when(chambreService.getChambresParNomUniversite("ESPRIT")).thenReturn(chambres);

        assertSame(chambres, controller.getChambresParNomUniversite("ESPRIT"));
    }

    @Test
    void getChambresParBlocEtTypeKeyWord() {
        List<Chambre> chambres = List.of(chambre());
        when(chambreService.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.SIMPLE)).thenReturn(chambres);

        assertSame(chambres, controller.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.SIMPLE));
    }

    @Test
    void getChambresParBlocEtTypeJPQL() {
        List<Chambre> chambres = List.of(chambre());
        when(chambreService.getChambresParBlocEtTypeJPQL(1L, TypeChambre.SIMPLE)).thenReturn(chambres);

        assertSame(chambres, controller.getChambresParBlocEtTypeJPQL(1L, TypeChambre.SIMPLE));
    }

    @Test
    void getChambresNonReserveParNomUniversiteEtTypeChambre() {
        List<Chambre> chambres = List.of(chambre());
        when(chambreService.getChambresNonReserveParNomUniversiteEtTypeChambre("ESPRIT", TypeChambre.SIMPLE))
                .thenReturn(chambres);

        assertSame(chambres, controller.getChambresNonReserveParNomUniversiteEtTypeChambre("ESPRIT", TypeChambre.SIMPLE));
    }
}
