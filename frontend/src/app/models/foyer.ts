/** Miroir de l'entité Java Foyer (les champs universite et blocs sont @JsonIgnore côté API). */
export interface Foyer {
  idFoyer?: number;
  nomFoyer: string;
  capaciteFoyer: number;
}
