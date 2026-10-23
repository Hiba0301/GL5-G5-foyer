package tn.esprit.tpfoyer17.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bloc{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idBloc;

    private String nomBloc;

    private long capaciteBloc;

    @ToString.Exclude
    @ManyToOne
    private Foyer foyer;

    @ToString.Exclude
    @OneToMany(mappedBy = "bloc")
    private Set<Chambre> chambres;
}