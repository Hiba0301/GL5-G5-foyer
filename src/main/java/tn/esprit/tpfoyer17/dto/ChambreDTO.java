package tn.esprit.tpfoyer17.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChambreDTO {
    private long idChambre;
    private long numeroChambre;
    private TypeChambre typeChambre;
}
