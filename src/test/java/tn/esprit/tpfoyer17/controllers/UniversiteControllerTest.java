package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.UniversiteDTO;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.services.IUniversiteService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversiteControllerTest {

    @Mock
    IUniversiteService universiteService;

    @InjectMocks
    UniversiteController controller;

    private Universite universite() {
        return Universite.builder().idUniversite(1L).nomUniversite("ESPRIT").adresse("Ariana").build();
    }

    @Test
    void addingUniversite() {
        when(universiteService.addUniversite(any(Universite.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("ESPRIT", controller.addingUniversite(new UniversiteDTO(0L, "ESPRIT", "Ariana", null)).getNomUniversite());
    }

    @Test
    void gettingAllUniversite() {
        when(universiteService.getAllUniversites()).thenReturn(List.of(universite()));

        assertEquals(1, controller.gettingAllUniversite().size());
    }

    @Test
    void gettingUniversite() {
        when(universiteService.getUniversiteById(1L)).thenReturn(universite());

        assertEquals("ESPRIT", controller.gettingUniversite(1L).getNomUniversite());
    }

    @Test
    void deletingUniversite() {
        controller.deletingUniversite(1L);

        verify(universiteService).deleteUniversite(1L);
    }

    @Test
    void updatingUniversite() {
        when(universiteService.updateUniversite(any(Universite.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Ariana", controller.updatingUniversite(new UniversiteDTO(1L, "ESPRIT", "Ariana", null)).getAdresse());
    }

    @Test
    void affecterFoyerAUniversite() {
        Universite universite = universite();
        universite.setFoyer(Foyer.builder().idFoyer(3L).nomFoyer("Foyer El Amal").build());
        when(universiteService.affecterFoyerAUniversite(3L, "ESPRIT")).thenReturn(universite);

        assertEquals(3L, controller.affecterFoyerAUniversite(3L, "ESPRIT").getFoyer().getIdFoyer());
    }

    @Test
    void desaffecterFoyerAUniversite() {
        when(universiteService.desaffecterFoyerAUniversite(1L)).thenReturn(universite());

        assertNull(controller.desaffecterFoyerAUniversite(1L).getFoyer());
    }
}
