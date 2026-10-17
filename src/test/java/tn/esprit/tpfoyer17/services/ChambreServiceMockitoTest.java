package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChambreServiceMockitoTest {

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ChambreService chambreService;

    @Test
    void testAddChambre() {
        Chambre chambre = createChambre(101L, TypeChambre.SIMPLE);
        when(chambreRepository.save(chambre)).thenReturn(chambre);

        Chambre result = chambreService.addChambre(chambre);

        assertNotNull(result);
        assertEquals(101L, result.getNumeroChambre());
        assertEquals(TypeChambre.SIMPLE, result.getTypeChambre());
        verify(chambreRepository).save(chambre);
    }

    @Test
    void testRetrieveChambre() {
        Chambre chambre = createChambre(202L, TypeChambre.DOUBLE);
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        Chambre result = chambreService.getChambreById(1L);

        assertNotNull(result);
        assertEquals(202L, result.getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, result.getTypeChambre());
        verify(chambreRepository).findById(1L);
    }

    @Test
    void testGetAllChambres() {
        List<Chambre> chambres = List.of(
                createChambre(101L, TypeChambre.SIMPLE),
                createChambre(202L, TypeChambre.DOUBLE));
        when(chambreRepository.findAll()).thenReturn(chambres);

        List<Chambre> result = chambreService.getAllChambres();

        assertNotNull(result);
        assertEquals(chambres, result);
        verify(chambreRepository).findAll();
    }

    @Test
    void testGetChambresParBlocEtTypeKeyWord() {
        Chambre chambre = createChambre(101L, TypeChambre.SIMPLE);
        List<Chambre> expected = List.of(chambre);
        when(chambreRepository.findByBlocIdBlocAndTypeChambre(1L, TypeChambre.SIMPLE))
                .thenReturn(expected);

        List<Chambre> result = chambreService.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.SIMPLE);

        assertEquals(expected, result);
        verify(chambreRepository).findByBlocIdBlocAndTypeChambre(1L, TypeChambre.SIMPLE);
    }

    @Test
    void testUpdateChambre() {
        Chambre chambre = createChambre(303L, TypeChambre.TRIPLE);
        when(chambreRepository.save(chambre)).thenReturn(chambre);

        Chambre result = chambreService.updateChambre(chambre);

        assertNotNull(result);
        assertEquals(303L, result.getNumeroChambre());
        assertEquals(TypeChambre.TRIPLE, result.getTypeChambre());
        verify(chambreRepository).save(chambre);
    }

    @Test
    void testDeleteChambre() {
        chambreService.deleteChambre(1L);

        verify(chambreRepository).deleteById(1L);
    }

    @Test
    void testRetrieveChambreWithMissingId() {
        when(chambreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> chambreService.getChambreById(99L));

        verify(chambreRepository).findById(99L);
    }

    private Chambre createChambre(long numero, TypeChambre type) {
        return Chambre.builder()
                .numeroChambre(numero)
                .typeChambre(type)
                .build();
    }
}
