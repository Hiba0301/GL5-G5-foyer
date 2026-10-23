import { Component, inject, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Bloc } from '../../models/bloc';
import { BlocService } from '../../services/bloc.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-bloc-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './bloc-list.html',
  styleUrl: './bloc-list.scss',
})
export class BlocList implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly blocService = inject(BlocService);

  protected readonly blocs = signal<Bloc[]>([]);
  protected readonly chargement = signal(false);
  protected readonly enregistrement = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);
  protected readonly idEdition = signal<number | null>(null);

  protected readonly form = this.formBuilder.group({
    nomBloc: ['', [Validators.required, Validators.pattern(/.*\S.*/), Validators.maxLength(100)]],
    capaciteBloc: [1, [Validators.required, Validators.min(1), Validators.pattern(/^[1-9]\d*$/)]],
  });

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    this.blocService.getAll()
      .pipe(finalize(() => this.chargement.set(false)))
      .subscribe({
        next: (blocs) => this.blocs.set(blocs),
        error: (error: unknown) => {
          this.erreur.set(messageErreur(error, 'Chargement des blocs'));
        },
      });
  }

  protected commencerEdition(bloc: Bloc): void {
    this.idEdition.set(bloc.idBloc);
    this.form.setValue({
      nomBloc: bloc.nomBloc,
      capaciteBloc: bloc.capaciteBloc,
    });
    this.erreur.set(null);
    this.succes.set(null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  protected annulerEdition(): void {
    this.reinitialiserFormulaire();
    this.erreur.set(null);
    this.succes.set(null);
  }

  protected enregistrer(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.enregistrement()) {
      return;
    }

    const valeurs = this.form.getRawValue();
    const payload = {
      nomBloc: valeurs.nomBloc.trim(),
      capaciteBloc: valeurs.capaciteBloc,
    };
    const idBloc = this.idEdition();
    const request = idBloc === null
      ? this.blocService.add(payload)
      : this.blocService.update({ idBloc, ...payload });

    this.enregistrement.set(true);
    this.erreur.set(null);
    this.succes.set(null);

    request
      .pipe(finalize(() => this.enregistrement.set(false)))
      .subscribe({
        next: (bloc) => {
          this.succes.set(idBloc === null ? 'Le bloc a été ajouté.' : 'Le bloc a été modifié.');
          this.mettreAJourListe(bloc);
          this.reinitialiserFormulaire();
        },
        error: (error: unknown) => {
          this.erreur.set(messageErreur(error, 'Enregistrement du bloc'));
        },
      });
  }

  protected supprimer(bloc: Bloc): void {
    if (!window.confirm(`Supprimer le bloc « ${bloc.nomBloc} » ?`)) {
      return;
    }

    this.erreur.set(null);
    this.succes.set(null);
    this.blocService.delete(bloc.idBloc).subscribe({
      next: () => {
        this.blocs.update((items) => items.filter((item) => item.idBloc !== bloc.idBloc));
        this.succes.set('Le bloc a été supprimé.');
        if (this.idEdition() === bloc.idBloc) {
          this.reinitialiserFormulaire();
        }
      },
      error: (error: unknown) => {
        this.erreur.set(messageErreur(error, 'Suppression du bloc'));
      },
    });
  }

  private mettreAJourListe(bloc: Bloc): void {
    this.blocs.update((items) => {
      const existe = items.some((item) => item.idBloc === bloc.idBloc);
      return existe
        ? items.map((item) => item.idBloc === bloc.idBloc ? bloc : item)
        : [...items, bloc];
    });
  }

  private reinitialiserFormulaire(): void {
    this.idEdition.set(null);
    this.form.reset({ nomBloc: '', capaciteBloc: 1 });
  }
}
