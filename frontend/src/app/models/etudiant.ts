/** Miroir de l'entité Java Etudiant. */
export interface Etudiant {
  idEtudiant?: number;
  nomEtudiant: string;
  prenomEtudiant: string;
  cinEtudiant: number;
  dateNaissance?: string | null;
}
