package tn.esprit.tpfoyer17.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation {

    // Id métier (ex : "3-2026") : assigné par le service, donc pas de @GeneratedValue
    @Id
    String idReservation;

    LocalDate anneeUniversitaire;

    boolean estValide;

    @ToString.Exclude
    @ManyToMany
    Set<Etudiant> etudiants;
}