import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Etudiant } from '../../models/etudiant';
import { EtudiantService } from '../../services/etudiant.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-etudiant-list',
  imports: [RouterLink],
  templateUrl: './etudiant-list.html',
})
export class EtudiantList implements OnInit {
  private readonly etudiantService = inject(EtudiantService);

  protected readonly etudiants = signal<Etudiant[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.etudiantService.getAll().subscribe({
      next: (etudiants) => {
        this.etudiants.set(etudiants);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set(messageErreur(err, 'Chargement des étudiants'));
        this.chargement.set(false);
      },
    });
  }

  supprimer(etudiant: Etudiant): void {
    if (!confirm(`Supprimer l'étudiant « ${etudiant.nomEtudiant} ${etudiant.prenomEtudiant} » ?`)) {
      return;
    }

    this.erreur.set(null);
    this.etudiantService.delete(etudiant.idEtudiant!).subscribe({
      next: () => {
        this.succes.set(`Étudiant « ${etudiant.nomEtudiant} ${etudiant.prenomEtudiant} » supprimé.`);
        this.charger();
      },
      error: (err) => this.erreur.set(messageErreur(err, 'Suppression de l\'étudiant')),
    });
  }

  formatDate(date?: string | null): string {
    if (!date) {
      return '—';
    }

    const valeur = new Date(date);
    if (Number.isNaN(valeur.getTime())) {
      return date;
    }

    return valeur.toLocaleDateString('fr-FR');
  }
}
