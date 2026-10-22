package tn.esprit.tpfoyer17.dto;

import org.junit.jupiter.api.Test;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoMappingTest {

    private static final ZoneId ZONE = ZoneId.of("Africa/Tunis");

    @Test
    void foyerDtoMapsAllFields() {
        Foyer foyer = new FoyerDTO(1L, "Foyer A", 200L).toEntity();

        assertEquals(1L, foyer.getIdFoyer());
        assertEquals("Foyer A", foyer.getNomFoyer());
        assertEquals(200L, foyer.getCapaciteFoyer());
    }

    @Test
    void blocDtoMapsFieldsAndFoyer() {
        Bloc bloc = new BlocDTO(2L, "Bloc B", 50L, new FoyerDTO(1L, "Foyer A", 200L)).toEntity();

        assertEquals(2L, bloc.getIdBloc());
        assertEquals("Bloc B", bloc.getNomBloc());
        assertEquals(50L, bloc.getCapaciteBloc());
        assertEquals(1L, bloc.getFoyer().getIdFoyer());
    }

    @Test
    void blocDtoWithoutFoyerLeavesFoyerNull() {
        Bloc bloc = new BlocDTO(2L, "Bloc B", 50L, null).toEntity();

        assertNull(bloc.getFoyer());
    }

    @Test
    void chambreDtoMapsFields() {
        Chambre chambre = new ChambreDTO(3L, 101L, TypeChambre.DOUBLE).toEntity();

        assertEquals(3L, chambre.getIdChambre());
        assertEquals(101L, chambre.getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, chambre.getTypeChambre());
    }

    @Test
    void etudiantDtoConvertsLocalDateToDate() {
        LocalDate naissance = LocalDate.of(2000, 5, 14);

        Etudiant etudiant = new EtudiantDTO(4L, "Ben Ali", "Sami", 12345678L, naissance).toEntity();

        assertEquals(4L, etudiant.getIdEtudiant());
        assertEquals("Ben Ali", etudiant.getNomEtudiant());
        assertEquals("Sami", etudiant.getPrenomEtudiant());
        assertEquals(12345678L, etudiant.getCinEtudiant());
        assertEquals(naissance, etudiant.getDateNaissance().toInstant().atZone(ZONE).toLocalDate());
    }

    @Test
    void etudiantDtoWithoutDateKeepsDateNull() {
        Etudiant etudiant = new EtudiantDTO(4L, "Ben Ali", "Sami", 1L, null).toEntity();

        assertNull(etudiant.getDateNaissance());
    }

    @Test
    void reservationDtoMapsEtudiantsAndDate() {
        LocalDate annee = LocalDate.of(2026, 9, 1);
        List<EtudiantDTO> etudiants = List.of(new EtudiantDTO(4L, "Ben Ali", "Sami", 1L, null));

        Reservation reservation = new ReservationDTO("3-2026", annee, true, etudiants).toEntity();

        assertEquals("3-2026", reservation.getIdReservation());
        assertTrue(reservation.isEstValide());
        assertEquals(annee, reservation.getAnneeUniversitaire().toInstant().atZone(ZONE).toLocalDate());
        assertEquals(1, reservation.getEtudiants().size());
        assertEquals(4L, reservation.getEtudiants().iterator().next().getIdEtudiant());
    }

    @Test
    void reservationDtoWithoutOptionalFieldsKeepsThemNull() {
        Reservation reservation = new ReservationDTO("3-2026", null, false, null).toEntity();

        assertNull(reservation.getAnneeUniversitaire());
        assertNull(reservation.getEtudiants());
        assertFalse(reservation.isEstValide());
    }

    @Test
    void universiteDtoMapsFieldsAndFoyer() {
        Universite universite = new UniversiteDTO(5L, "ESPRIT", "Ariana",
                new FoyerDTO(1L, "Foyer A", 200L)).toEntity();

        assertEquals(5L, universite.getIdUniversite());
        assertEquals("ESPRIT", universite.getNomUniversite());
        assertEquals("Ariana", universite.getAdresse());
        assertEquals(1L, universite.getFoyer().getIdFoyer());
    }

    @Test
    void universiteDtoWithoutFoyerLeavesFoyerNull() {
        Universite universite = new UniversiteDTO(5L, "ESPRIT", "Ariana", null).toEntity();

        assertNull(universite.getFoyer());
    }
}
