/** Miroir de l'entité Java Chambre (bloc et reservations sont @JsonIgnore côté API). */
export interface Chambre {
  idChambre?: number;
  numeroChambre: number;
  typeChambre: TypeChambre;
}

export type TypeChambre = 'SIMPLE' | 'DOUBLE' | 'TRIPLE';
