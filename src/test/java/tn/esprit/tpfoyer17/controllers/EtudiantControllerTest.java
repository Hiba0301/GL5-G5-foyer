package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.services.IEtudiantService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EtudiantControllerTest {

    @Mock
    IEtudiantService etudiantService;

    @InjectMocks
    EtudiantController controller;

    private Etudiant etudiant() {
        return Etudiant.builder().idEtudiant(1L).nomEtudiant("Ben Ali").prenomEtudiant("Salima").cinEtudiant(12345678L).build();
    }

    @Test
    void addEtudiant() {
        Etudiant etudiant = etudiant();
        when(etudiantService.addEtudiant(etudiant)).thenReturn(etudiant);

        assertSame(etudiant, controller.addEtudiant(etudiant));
    }

    @Test
    void gettingAllEtudiant() {
        when(etudiantService.getAllEtudiants()).thenReturn(List.of(etudiant(), etudiant()));

        assertEquals(2, controller.gettingAllEtudiant().size());
    }

    @Test
    void gettingEtudiant() {
        when(etudiantService.getEtudiantById(1L)).thenReturn(etudiant());

        assertEquals("Ben Ali", controller.gettingEtudiant(1L).getNomEtudiant());
    }

    @Test
    void deletingEtudiant() {
        controller.deletingEtudiant(1L);

        verify(etudiantService).deleteEtudiant(1L);
    }

    @Test
    void updatingEtudiant() {
        Etudiant etudiant = etudiant();
        when(etudiantService.updateEtudiant(etudiant)).thenReturn(etudiant);

        assertSame(etudiant, controller.updatingEtudiant(etudiant));
    }
}
