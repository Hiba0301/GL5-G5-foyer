package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.EtudiantDTO;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.services.IEtudiantService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        when(etudiantService.addEtudiant(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(12345678L, controller.addEtudiant(new EtudiantDTO(0L, "Ben Ali", "Salima", 12345678L, null)).getCinEtudiant());
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
        when(etudiantService.updateEtudiant(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Salima", controller.updatingEtudiant(new EtudiantDTO(1L, "Ben Ali", "Salima", 12345678L, null)).getPrenomEtudiant());
    }
}
