package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.dto.BlocDTO;
import tn.esprit.tpfoyer17.dto.ChambreDTO;
import tn.esprit.tpfoyer17.dto.EtudiantDTO;
import tn.esprit.tpfoyer17.dto.FoyerDTO;
import tn.esprit.tpfoyer17.dto.ReservationDTO;
import tn.esprit.tpfoyer17.dto.UniversiteDTO;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;
import tn.esprit.tpfoyer17.services.IBlocService;
import tn.esprit.tpfoyer17.services.IChambreService;
import tn.esprit.tpfoyer17.services.IEtudiantService;
import tn.esprit.tpfoyer17.services.IFoyerService;
import tn.esprit.tpfoyer17.services.IReservationService;
import tn.esprit.tpfoyer17.services.IUniversiteService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllersUnitTest {

    @Mock private IBlocService blocService;
    @Mock private IChambreService chambreService;
    @Mock private IFoyerService foyerService;
    @Mock private IEtudiantService etudiantService;
    @Mock private EtudiantRepository etudiantRepository;
    @Mock private IReservationService reservationService;
    @Mock private IUniversiteService universiteService;

    private BlocController blocController;
    private ChambreController chambreController;
    private FoyerController foyerController;
    private EtudiantController etudiantController;
    private ReservationController reservationController;
    private UniversiteController universiteController;

    @BeforeEach
    void setUp() {
        blocController = new BlocController(blocService);
        chambreController = new ChambreController(chambreService);
        foyerController = new FoyerController(foyerService);
        etudiantController = new EtudiantController(etudiantService, etudiantRepository);
        reservationController = new ReservationController(reservationService);
        universiteController = new UniversiteController(universiteService);
    }

    @Test
    void blocAddConvertsDtoBeforeCallingService() {
        when(blocService.addBloc(any(Bloc.class))).thenAnswer(i -> i.getArgument(0));

        Bloc result = blocController.addingBloc(new BlocDTO(1L, "Bloc A", 30L, null));

        assertEquals("Bloc A", result.getNomBloc());
        assertEquals(30L, result.getCapaciteBloc());
    }

    @Test
    void blocUpdateConvertsDtoBeforeCallingService() {
        when(blocService.updateBloc(any(Bloc.class))).thenAnswer(i -> i.getArgument(0));

        Bloc result = blocController.updatingBloc(new BlocDTO(1L, "Bloc A", 30L, new FoyerDTO(9L, "F", 1L)));

        assertEquals(1L, result.getIdBloc());
        assertEquals(9L, result.getFoyer().getIdFoyer());
    }

    @Test
    void chambreAddConvertsDtoBeforeCallingService() {
        when(chambreService.addChambre(any(Chambre.class))).thenAnswer(i -> i.getArgument(0));

        Chambre result = chambreController.addingChambre(new ChambreDTO(2L, 101L, TypeChambre.SIMPLE, 1L));

        assertEquals(101L, result.getNumeroChambre());
        assertEquals(TypeChambre.SIMPLE, result.getTypeChambre());
    }

    @Test
    void chambreUpdateConvertsDtoBeforeCallingService() {
        when(chambreService.updateChambre(any(Chambre.class))).thenAnswer(i -> i.getArgument(0));

        Chambre result = chambreController.updatingChambre(new ChambreDTO(2L, 102L, TypeChambre.DOUBLE, 1L));

        assertEquals(2L, result.getIdChambre());
        assertEquals(102L, result.getNumeroChambre());
    }

    @Test
    void chambreParBlocEtTypeKeyWordDelegatesToService() {
        List<Chambre> attendu = List.of(Chambre.builder().idChambre(7L).build());
        when(chambreService.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.SIMPLE)).thenReturn(attendu);

        List<Chambre> result = chambreController.getChambresParBlocEtTypeKeyWord(1L, TypeChambre.SIMPLE);

        assertSame(attendu, result);
    }

    @Test
    void etudiantAddConvertsDtoBeforeCallingService() {
        LocalDate naissance = LocalDate.of(2000, 5, 14);
        when(etudiantService.addEtudiant(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        Etudiant result = etudiantController.addEtudiant(new EtudiantDTO(3L, "Ben Ali", "Sami", 99L, naissance));

        assertEquals("Sami", result.getPrenomEtudiant());
        assertEquals(99L, result.getCinEtudiant());
        assertNotNull(result.getDateNaissance());
    }

    @Test
    void etudiantUpdateConvertsDtoBeforeCallingService() {
        when(etudiantService.updateEtudiant(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        Etudiant result = etudiantController.updatingEtudiant(new EtudiantDTO(3L, "Ben Ali", "Sami", 99L, null));

        assertEquals(3L, result.getIdEtudiant());
        assertEquals("Ben Ali", result.getNomEtudiant());
    }

    @Test
    void foyerAddConvertsDtoBeforeCallingService() {
        when(foyerService.addFoyer(any(Foyer.class))).thenAnswer(i -> i.getArgument(0));

        Foyer result = foyerController.addingFoyer(new FoyerDTO(1L, "Foyer A", 200L));

        assertEquals("Foyer A", result.getNomFoyer());
    }

    @Test
    void foyerUpdateConvertsDtoBeforeCallingService() {
        when(foyerService.updateFoyer(any(Foyer.class))).thenAnswer(i -> i.getArgument(0));

        Foyer result = foyerController.updatingFoyer(new FoyerDTO(1L, "Foyer B", 300L));

        assertEquals(300L, result.getCapaciteFoyer());
    }

    @Test
    void foyerAjouterEtAffecterConvertsDtoBeforeCallingService() {
        when(foyerService.ajouterFoyerEtAffecterAUniversite(any(Foyer.class), eq(9L)))
                .thenAnswer(i -> i.getArgument(0));

        Foyer result = foyerController.ajouterFoyerEtAffecterAUniversite(new FoyerDTO(1L, "Foyer C", 50L), 9L);

        assertEquals("Foyer C", result.getNomFoyer());
    }

    @Test
    void reservationUpdateConvertsDtoBeforeCallingService() {
        when(reservationService.updateReservation(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation result = reservationController.updatingReservation(
                new ReservationDTO("3-2026", LocalDate.of(2026, 9, 1), true, null));

        assertEquals("3-2026", result.getIdReservation());
        assertTrue(result.isEstValide());
    }

    @Test
    void universiteAddConvertsDtoBeforeCallingService() {
        when(universiteService.addUniversite(any(Universite.class))).thenAnswer(i -> i.getArgument(0));

        Universite result = universiteController.addingUniversite(
                new UniversiteDTO(5L, "ESPRIT", "Ariana", new FoyerDTO(1L, "Foyer A", 200L)));

        assertEquals("ESPRIT", result.getNomUniversite());
        assertEquals(1L, result.getFoyer().getIdFoyer());
    }

    @Test
    void universiteUpdateConvertsDtoBeforeCallingService() {
        when(universiteService.updateUniversite(any(Universite.class))).thenAnswer(i -> i.getArgument(0));

        Universite result = universiteController.updatingUniversite(
                new UniversiteDTO(5L, "ESPRIT", "Tunis", null));

        assertEquals("Tunis", result.getAdresse());
        assertEquals(null, result.getFoyer());
    }
}
