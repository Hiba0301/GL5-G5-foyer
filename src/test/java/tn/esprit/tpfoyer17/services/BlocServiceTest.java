package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private BlocService blocService;

    @Test
    void testAddBlocAvecMockito() {
        Bloc bloc = creerBloc("Bloc Mockito", 100);
        when(blocRepository.save(bloc)).thenReturn(bloc);

        Bloc resultat = blocService.addBloc(bloc);

        assertNotNull(resultat);
        assertEquals("Bloc Mockito", resultat.getNomBloc());
        verify(blocRepository).save(bloc);
    }

    @Test
    void testGetAllBlocsAvecMockito() {
        when(blocRepository.findAll()).thenReturn(List.of(creerBloc("Bloc A", 50)));

        assertEquals(1, blocService.getAllBlocs().size());
    }

    @Test
    void testGetBlocByIdAvecMockito() {
        Bloc bloc = creerBloc("Bloc recherche", 80);
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        Bloc resultat = blocService.getBlocById(1L);

        assertNotNull(resultat);
        assertEquals("Bloc recherche", resultat.getNomBloc());
        verify(blocRepository).findById(1L);
    }

    @Test
    void testGetBlocByIdIntrouvable() {
        when(blocRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> blocService.getBlocById(99L));
    }

    @Test
    void testUpdateBlocAvecMockito() {
        Bloc bloc = creerBloc("Bloc modifie", 120);
        when(blocRepository.save(bloc)).thenReturn(bloc);

        Bloc resultat = blocService.updateBloc(bloc);

        assertEquals("Bloc modifie", resultat.getNomBloc());
        assertEquals(120, resultat.getCapaciteBloc());
        verify(blocRepository).save(bloc);
    }

    @Test
    void testDeleteBlocAvecMockito() {
        doNothing().when(blocRepository).deleteById(1L);

        blocService.deleteBloc(1L);

        verify(blocRepository).deleteById(1L);
    }

    @Test
    void testAffecterChambresABloc() {
        Bloc bloc = creerBloc("Bloc A", 50);
        Chambre chambre = mock(Chambre.class);
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));
        when(chambreRepository.findAllById(List.of(5L))).thenReturn(List.of(chambre));

        Bloc resultat = blocService.affecterChambresABloc(List.of(5L), 1L);

        assertSame(bloc, resultat);
        verify(chambre).setBloc(bloc);
        verify(chambreRepository).saveAll(List.of(chambre));
    }

    @Test
    void testAffecterChambresABlocIntrouvable() {
        when(blocRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> blocService.affecterChambresABloc(List.of(5L), 99L));
    }

    private Bloc creerBloc(String nom, long capacite) {
        return Bloc.builder().nomBloc(nom).capaciteBloc(capacite).build();
    }
}