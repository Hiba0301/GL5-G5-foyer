package tn.esprit.tpfoyer17.services;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class ChambreServiceIntegrationTest {

    @Autowired
    private IChambreService chambreService;

    @Autowired
    private BlocRepository blocRepository;

    @Test
    void testAddChambre() {
        Chambre chambre = createChambre(101L, TypeChambre.SIMPLE);

        Chambre result = chambreService.addChambre(chambre);

        assertNotNull(result);
        assertTrue(result.getIdChambre() > 0);
        assertEquals(101L, result.getNumeroChambre());
        assertEquals(TypeChambre.SIMPLE, result.getTypeChambre());
        log.info("Added chambre with id {}", result.getIdChambre());
    }

    @Test
    void testRetrieveChambre() {
        Chambre chambre = createChambre(202L, TypeChambre.DOUBLE);
        Chambre saved = chambreService.addChambre(chambre);

        Chambre result = chambreService.getChambreById(saved.getIdChambre());

        assertNotNull(result);
        assertEquals(saved.getIdChambre(), result.getIdChambre());
        assertEquals(202L, result.getNumeroChambre());
        assertTrue(chambreService.getAllChambres().stream()
                .anyMatch(item -> item.getIdChambre() == saved.getIdChambre()));
        log.info("Retrieved chambre with id {}", result.getIdChambre());
    }

    @Test
    void testGetChambresParBlocEtType() {
        Bloc bloc = blocRepository.save(Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(50)
                .build());

        Chambre simple = chambreService.addChambre(Chambre.builder()
                .numeroChambre(1L)
                .typeChambre(TypeChambre.SIMPLE)
                .bloc(bloc)
                .build());
        chambreService.addChambre(Chambre.builder()
                .numeroChambre(2L)
                .typeChambre(TypeChambre.DOUBLE)
                .bloc(bloc)
                .build());

        List<Chambre> keyword = chambreService.getChambresParBlocEtTypeKeyWord(
                bloc.getIdBloc(), TypeChambre.SIMPLE);
        List<Chambre> jpql = chambreService.getChambresParBlocEtTypeJPQL(
                bloc.getIdBloc(), TypeChambre.SIMPLE);

        assertEquals(List.of(simple), keyword);
        assertEquals(List.of(simple), jpql);
    }

    @Test
    void testUpdateChambre() {
        Chambre chambre = chambreService.addChambre(createChambre(303L, TypeChambre.SIMPLE));

        chambre.setNumeroChambre(304L);
        chambre.setTypeChambre(TypeChambre.TRIPLE);

        Chambre result = chambreService.updateChambre(chambre);

        assertNotNull(result);
        assertEquals(chambre.getIdChambre(), result.getIdChambre());
        assertEquals(304L, result.getNumeroChambre());
        assertEquals(TypeChambre.TRIPLE, result.getTypeChambre());
        log.info("Updated chambre with id {}", result.getIdChambre());
    }

    @Test
    void testUpdateChambreWithMissingIdDoesNotCreateChambre() {
        long countBefore = chambreService.getAllChambres().size();
        Chambre chambre = createChambre(999999L, 505L, TypeChambre.DOUBLE);

        assertThrows(NoSuchElementException.class, () -> chambreService.updateChambre(chambre));

        assertEquals(countBefore, chambreService.getAllChambres().size());
    }

    @Test
    void testDeleteChambre() {
        Chambre chambre = chambreService.addChambre(createChambre(404L, TypeChambre.DOUBLE));
        long id = chambre.getIdChambre();

        chambreService.deleteChambre(id);

        assertThrows(NoSuchElementException.class, () -> chambreService.getChambreById(id));
        assertTrue(chambreService.getAllChambres().stream()
                .noneMatch(item -> item.getIdChambre() == id));
        log.info("Deleted chambre with id {}", id);
    }

    private Chambre createChambre(long id, long numero, TypeChambre type) {
        return Chambre.builder()
                .idChambre(id)
                .numeroChambre(numero)
                .typeChambre(type)
                .build();
    }

    private Chambre createChambre(long numero, TypeChambre type) {
        return Chambre.builder()
                .numeroChambre(numero)
                .typeChambre(type)
                .build();
    }
}
