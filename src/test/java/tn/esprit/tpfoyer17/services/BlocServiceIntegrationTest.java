package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpfoyer17.entities.Bloc;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        long id = bloc.getIdBloc();

        blocService.deleteBloc(id);

        assertThrows(NoSuchElementException.class, () -> blocService.getBlocById(id));
    }

    private Bloc creerBloc(String nom, long capacite) {
        return Bloc.builder()
                .nomBloc(nom)
                .capaciteBloc(capacite)
                .build();
    }
}