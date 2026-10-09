package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.repositories.BlocRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

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
    void testGetBlocByIdAvecMockito() {
        Bloc bloc = creerBloc("Bloc recherche", 80);
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        Bloc resultat = blocService.getBlocById(1L);

        assertNotNull(resultat);
        assertEquals("Bloc recherche", resultat.getNomBloc());
        verify(blocRepository).findById(1L);
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

    private Bloc creerBloc(String nom, long capacite) {
        return Bloc.builder()
                .nomBloc(nom)
                .capaciteBloc(capacite)
                .build();
    }
}

@SpringBootTest
class BlocServiceIntegrationTest {

    @Autowired
    private IBlocService blocService;

    @Test
    void testAddBlocSansMockito() {
        Bloc bloc = blocService.addBloc(creerBloc("Bloc Integration Add", 100));

        assertTrue(bloc.getIdBloc() > 0);
        blocService.deleteBloc(bloc.getIdBloc());
    }

    @Test
    void testGetBlocByIdSansMockito() {
        Bloc bloc = blocService.addBloc(creerBloc("Bloc Integration Get", 80));

        Bloc resultat = blocService.getBlocById(bloc.getIdBloc());

        assertEquals(bloc.getIdBloc(), resultat.getIdBloc());
        blocService.deleteBloc(bloc.getIdBloc());
    }

    @Test
    void testUpdateBlocSansMockito() {
        Bloc bloc = blocService.addBloc(creerBloc("Bloc avant modification", 80));
        bloc.setNomBloc("Bloc apres modification");

        Bloc resultat = blocService.updateBloc(bloc);

        assertEquals("Bloc apres modification", resultat.getNomBloc());
        blocService.deleteBloc(bloc.getIdBloc());
    }

    @Test
    void testDeleteBlocSansMockito() {
        Bloc bloc = blocService.addBloc(creerBloc("Bloc Integration Delete", 60));

        blocService.deleteBloc(bloc.getIdBloc());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> blocService.getBlocById(bloc.getIdBloc())
        );
    }

    private Bloc creerBloc(String nom, long capacite) {
        return Bloc.builder()
                .nomBloc(nom)
                .capaciteBloc(capacite)
                .build();
    }
}
