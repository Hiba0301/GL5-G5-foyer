import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { Chambre, TypeChambre } from '../../models/chambre';
import { ChambreService } from '../../services/chambre.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-chambre-list',
  imports: [ReactiveFormsModule, RouterLink],
  styleUrl: './chambre-list.scss',
  templateUrl: './chambre-list.html',
})
export class ChambreList implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly chambreService = inject(ChambreService);

  protected readonly chambres = signal<Chambre[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);
  protected readonly types: TypeChambre[] = ['SIMPLE', 'DOUBLE', 'TRIPLE'];
  protected readonly filtres = [
    { value: 'all', label: 'Toutes les chambres' },
    { value: 'universite', label: 'Université' },
    { value: 'bloc', label: 'Bloc et type (requête dérivée)' },
    { value: 'jpql', label: 'Bloc et type (JPQL)' },
    { value: 'nonReserve', label: 'Chambres non réservées' },
  ] as const;

  protected readonly filtre = this.fb.nonNullable.group({
    mode: ['all' as (typeof this.filtres)[number]['value']],
    nomUniversite: [''],
    idBloc: [''],
    type: ['SIMPLE' as TypeChambre],
  });

  ngOnInit(): void {
    const succes = history.state?.succes as string | undefined;
    if (succes) {
      this.afficherSucces(succes);
    }
    this.charger();
  }

  charger(): void {
    this.executer(this.chambreService.getAll(), 'Chargement des chambres');
  }

  changementFiltre(): void {
    this.filtre.patchValue({
      nomUniversite: '',
      idBloc: '',
      type: 'SIMPLE',
    });
    this.erreur.set(null);
  }

  reinitialiser(): void {
    this.filtre.reset({
      mode: 'all',
      nomUniversite: '',
      idBloc: '',
      type: 'SIMPLE',
    });
    this.erreur.set(null);
    this.charger();
  }

  rechercher(): void {
    const { mode, nomUniversite, idBloc, type } = this.filtre.getRawValue();
    if (mode === 'all') {
      this.charger();
      return;
    }
    if ((mode === 'universite' || mode === 'nonReserve') && !nomUniversite.trim()) {
      this.erreur.set('Saisissez le nom d’une université.');
      return;
    }
    const id = Number(idBloc);
    if ((mode === 'bloc' || mode === 'jpql') && (!Number.isInteger(id) || id < 1)) {
      this.erreur.set('Saisissez un identifiant de bloc valide.');
      return;
    }

    const requete: Observable<Chambre[]> =
      mode === 'universite'
        ? this.chambreService.getByNomUniversite(nomUniversite.trim())
        : mode === 'bloc'
          ? this.chambreService.getByBlocAndType(id, type)
          : mode === 'jpql'
            ? this.chambreService.getByBlocAndTypeJpql(id, type)
            : this.chambreService.getNonReserveByNomUniversiteAndType(nomUniversite.trim(), type);
    const contexte =
      mode === 'universite'
        ? 'Recherche par université'
        : mode === 'nonReserve'
          ? 'Recherche des chambres non réservées'
          : 'Recherche par bloc et type';
    this.executer(requete, contexte);
  }

  rechercheDesactivee(): boolean {
    const { mode, nomUniversite, idBloc } = this.filtre.getRawValue();
    return (
      this.chargement() ||
      ((mode === 'universite' || mode === 'nonReserve') && !nomUniversite.trim()) ||
      ((mode === 'bloc' || mode === 'jpql') && !idBloc)
    );
  }

  supprimer(chambre: Chambre): void {
    if (!confirm(`Supprimer la chambre #${chambre.idChambre} ?`)) {
      return;
    }

    this.erreur.set(null);
    this.chambreService.delete(chambre.idChambre!).subscribe({
      next: () => {
        this.afficherSucces(`Chambre #${chambre.idChambre} supprimée.`);
        this.charger();
      },
      error: (err) => this.erreur.set(messageErreur(err, 'Suppression de la chambre')),
    });
  }

  private afficherSucces(message: string): void {
    this.succes.set(message);
    window.setTimeout(() => this.succes.set(null), 3000);
  }

  private executer(requete: Observable<Chambre[]>, contexte: string): void {
    this.chargement.set(true);
    this.erreur.set(null);
    requete.subscribe({
      next: (chambres) => {
        this.chambres.set(chambres);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set(messageErreur(err, contexte));
        this.chargement.set(false);
      },
    });
  }
}
