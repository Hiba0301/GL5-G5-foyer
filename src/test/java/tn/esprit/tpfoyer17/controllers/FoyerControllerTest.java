package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.services.IFoyerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoyerControllerTest {

    @Mock
    IFoyerService foyerService;

    @InjectMocks
    FoyerController controller;

    private Foyer foyer() {
        return Foyer.builder().idFoyer(1L).nomFoyer("Foyer El Amal").capaciteFoyer(300).build();
    }

    @Test
    void addingFoyer() {
        Foyer foyer = foyer();
        when(foyerService.addFoyer(foyer)).thenReturn(foyer);

        assertSame(foyer, controller.addingFoyer(foyer));
    }

    @Test
    void gettingAllFoyer() {
        when(foyerService.getAllFoyers()).thenReturn(List.of(foyer(), foyer()));

        assertEquals(2, controller.gettingAllFoyer().size());
    }

    @Test
    void gettingFoyer() {
        when(foyerService.getFoyerById(1L)).thenReturn(foyer());

        assertEquals("Foyer El Amal", controller.gettingFoyer(1L).getNomFoyer());
    }

    @Test
    void deletingFoyer() {
        controller.deletingFoyer(1L);

        verify(foyerService).deleteFoyer(1L);
    }

    @Test
    void updatingFoyer() {
        Foyer foyer = foyer();
        when(foyerService.updateFoyer(foyer)).thenReturn(foyer);

        assertEquals(300, controller.updatingFoyer(foyer).getCapaciteFoyer());
    }

    @Test
    void ajouterFoyerEtAffecterAUniversite() {
        Foyer foyer = foyer();
        when(foyerService.ajouterFoyerEtAffecterAUniversite(foyer, 5L)).thenReturn(foyer);

        assertSame(foyer, controller.ajouterFoyerEtAffecterAUniversite(foyer, 5L));
    }
}
