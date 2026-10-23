package tn.esprit.tpfoyer17.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.services.IReservationService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    IReservationService reservationService;

    @InjectMocks
    ReservationController controller;

    private Reservation reservation() {
        return Reservation.builder().idReservation("3-2026").estValide(true).build();
    }

    @Test
    void gettingAllReservation() {
        when(reservationService.getAllReservations()).thenReturn(List.of(reservation()));

        assertEquals(1, controller.gettingAllReservation().size());
    }

    @Test
    void gettingReservation() {
        when(reservationService.getReservationById("3-2026")).thenReturn(reservation());

        assertEquals("3-2026", controller.gettingReservation("3-2026").getIdReservation());
    }

    @Test
    void deletingReservation() {
        controller.deletingReservation("3-2026");

        verify(reservationService).deleteReservation("3-2026");
    }

    @Test
    void updatingReservation() {
        Reservation reservation = reservation();
        when(reservationService.updateReservation(reservation)).thenReturn(reservation);

        assertSame(reservation, controller.updatingReservation(reservation));
    }

    @Test
    void ajouterReservation() {
        Reservation reservation = reservation();
        when(reservationService.ajouterReservation(1L, 12345678L)).thenReturn(reservation);

        assertSame(reservation, controller.ajouterReservation(1L, 12345678L));
    }

    @Test
    void annulerReservation() {
        Reservation reservation = reservation();
        when(reservationService.annulerReservation(12345678L)).thenReturn(reservation);

        assertSame(reservation, controller.annulerReservation(12345678L));
    }

    @Test
    void getReservationParAnneeUniversitaireEtNomUniversite() {
        Date annee = new Date();
        List<Reservation> reservations = List.of(reservation());
        when(reservationService.getReservationParAnneeUniversitaireEtNomUniversite(annee, "ESPRIT")).thenReturn(reservations);

        assertSame(reservations, controller.getReservationParAnneeUniversitaireEtNomUniversite(annee, "ESPRIT"));
    }
}
