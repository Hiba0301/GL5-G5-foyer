package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.FoyerDTO;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.services.IFoyerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        when(foyerService.addFoyer(any(Foyer.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Foyer El Amal", controller.addingFoyer(new FoyerDTO(0L, "Foyer El Amal", 300L)).getNomFoyer());
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
        when(foyerService.updateFoyer(any(Foyer.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(300, controller.updatingFoyer(new FoyerDTO(1L, "Foyer El Amal", 300L)).getCapaciteFoyer());
    }

    @Test
    void ajouterFoyerEtAffecterAUniversite() {
        Foyer foyer = foyer();
        when(foyerService.ajouterFoyerEtAffecterAUniversite(any(Foyer.class), eq(5L))).thenReturn(foyer);

        assertSame(foyer, controller.ajouterFoyerEtAffecterAUniversite(new FoyerDTO(0L, "Foyer El Amal", 300L), 5L));
    }
}
