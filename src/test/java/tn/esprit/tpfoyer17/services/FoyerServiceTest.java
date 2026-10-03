package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.repositories.FoyerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FoyerServiceTest {

	@Mock
	private FoyerRepository foyerRepository;

	@InjectMocks
	private FoyerService foyerService;

	@Test
	void testAddFoyerAvecMockito() {
		Foyer foyer = creerFoyer("Foyer Mockito", 500);
		when(foyerRepository.save(foyer)).thenReturn(foyer);

		Foyer resultat = foyerService.addFoyer(foyer);

		assertNotNull(resultat);
		assertEquals("Foyer Mockito", resultat.getNomFoyer());
		verify(foyerRepository).save(foyer);
	}

	@Test
	void testGetFoyerByIdAvecMockito() {
		Foyer foyer = creerFoyer("Foyer recherche", 300);
		when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

		Foyer resultat = foyerService.getFoyerById(1L);

		assertNotNull(resultat);
		assertEquals("Foyer recherche", resultat.getNomFoyer());
		verify(foyerRepository).findById(1L);
	}

	@Test
	void testUpdateFoyerAvecMockito() {
		Foyer foyer = creerFoyer("Foyer modifie", 600);
		when(foyerRepository.save(foyer)).thenReturn(foyer);

		Foyer resultat = foyerService.updateFoyer(foyer);

		assertEquals("Foyer modifie", resultat.getNomFoyer());
		assertEquals(600, resultat.getCapaciteFoyer());
		verify(foyerRepository).save(foyer);
	}

	@Test
	void testDeleteFoyerAvecMockito() {
		doNothing().when(foyerRepository).deleteById(1L);

		foyerService.deleteFoyer(1L);

		verify(foyerRepository).deleteById(1L);
	}

	private Foyer creerFoyer(String nom, long capacite) {
		return Foyer.builder()
				.nomFoyer(nom)
				.capaciteFoyer(capacite)
				.build();
	}
}

@SpringBootTest
class FoyerServiceIntegrationTest {

	@Autowired
	private IFoyerService foyerService;

	@Test
	void testAddFoyerSansMockito() {
		Foyer foyer = creerFoyer("Foyer Integration Add", 400);

		Foyer resultat = foyerService.addFoyer(foyer);

		assertNotNull(resultat.getIdFoyer());
		foyerService.deleteFoyer(resultat.getIdFoyer());
	}

	@Test
	void testGetFoyerByIdSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer Integration Get", 400));

		Foyer resultat = foyerService.getFoyerById(foyer.getIdFoyer());

		assertEquals(foyer.getIdFoyer(), resultat.getIdFoyer());
		foyerService.deleteFoyer(foyer.getIdFoyer());
	}

	@Test
	void testUpdateFoyerSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer avant modification", 400));
		foyer.setNomFoyer("Foyer apres modification");

		Foyer resultat = foyerService.updateFoyer(foyer);

		assertEquals("Foyer apres modification", resultat.getNomFoyer());
		foyerService.deleteFoyer(foyer.getIdFoyer());
	}

	@Test
	void testDeleteFoyerSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer Integration Delete", 400));

		foyerService.deleteFoyer(foyer.getIdFoyer());

		org.junit.jupiter.api.Assertions.assertThrows(
				java.util.NoSuchElementException.class,
				() -> foyerService.getFoyerById(foyer.getIdFoyer())
		);
	}

	private Foyer creerFoyer(String nom, long capacite) {
		return Foyer.builder()
				.nomFoyer(nom)
				.capaciteFoyer(capacite)
				.build();
	}
}