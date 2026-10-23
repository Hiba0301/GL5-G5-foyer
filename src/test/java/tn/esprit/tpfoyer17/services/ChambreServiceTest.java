package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChambreServiceTest {

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ChambreService chambreService;

    private Chambre chambre() {
        return Chambre.builder().idChambre(1L).numeroChambre(101L).typeChambre(TypeChambre.DOUBLE).build();
    }

    @Test
    void testAddChambre() {
        Chambre chambre = chambre();
        when(chambreRepository.save(chambre)).thenReturn(chambre);

        assertSame(chambre, chambreService.addChambre(chambre));
    }

    @Test
    void testGetAllChambres() {
        when(chambreRepository.findAll()).thenReturn(List.of(chambre(), chambre()));

        assertEquals(2, chambreService.getAllChambres().size());
    }

    @Test
    void testGetChambreById() {
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre()));

        assertEquals(101L, chambreService.getChambreById(1L).getNumeroChambre());
    }

    @Test
    void testGetChambreByIdIntrouvable() {
        when(chambreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> chambreService.getChambreById(99L));
    }

    @Test
    void testDeleteChambre() {
        chambreService.deleteChambre(1L);

        verify(chambreRepository).deleteById(1L);
    }

    @Test
    void testUpdateChambre() {
        Chambre chambre = chambre();
        when(chambreRepository.save(chambre)).thenReturn(chambre);

        assertSame(chambre, chambreService.updateChambre(chambre));
    }

    @Test
    void testGetChambresParNomUniversite() {
        when(chambreRepository.findByBlocFoyerUniversiteNomUniversite("ESPRIT")).thenReturn(List.of(chambre()));

        assertEquals(1, chambreService.getChambresParNomUniversite("ESPRIT").size());
    }

    @Test
    void testGetChambresParBlocEtTypeKeyWord() {
        when(chambreRepository.findByBlocIdBlocAndTypeChambre(1L, TypeChambre.DOUBLE)).thenReturn(List.of(chambre()));

        assertEquals(1, chambreService.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.DOUBLE).size());
    }

    @Test
    void testGetChambresParBlocEtTypeJPQL() {
        when(chambreRepository.findByBlocIdBlocAndTypeChambreJPQL(1L, TypeChambre.DOUBLE)).thenReturn(List.of(chambre()));

        assertEquals(1, chambreService.getChambresParBlocEtTypeJPQL(1L, TypeChambre.DOUBLE).size());
    }

    @Test
    void testGetChambresNonReserveParNomUniversiteEtTypeChambre() {
        when(chambreRepository.getChambresNonReserveParNomUniversiteEtTypeChambre("ESPRIT", TypeChambre.SIMPLE))
                .thenReturn(List.of(chambre()));

        assertEquals(1, chambreService.getChambresNonReserveParNomUniversiteEtTypeChambre("ESPRIT", TypeChambre.SIMPLE).size());
    }

    @Test
    void testGetChambreNonReserverPlanifie() {
        when(chambreRepository.getChambresNonReserve()).thenReturn(List.of(chambre()));

        chambreService.getChambreNonReserver();

        verify(chambreRepository).getChambresNonReserve();
    }
}
