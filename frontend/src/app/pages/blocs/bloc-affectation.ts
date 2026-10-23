import { Component, inject, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Bloc } from '../../models/bloc';
import { BlocService } from '../../services/bloc.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-bloc-affectation',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './bloc-affectation.html',
  styleUrl: './bloc-affectation.scss',
})
export class BlocAffectation implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly blocService = inject(BlocService);

  protected readonly blocs = signal<Bloc[]>([]);
  protected readonly chargement = signal(false);
  protected readonly enregistrement = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);

  protected readonly form = this.formBuilder.group({
    idBloc: [0, [Validators.required, Validators.min(1)]],
    chambres: ['', Validators.required],
  });

  ngOnInit(): void {
    this.chargerBlocs();
  }

  protected affecter(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.enregistrement()) {
      return;
    }

    const idsTexte = this.form.controls.chambres.value.split(',').map((id) => id.trim());
    const ids = idsTexte.map(Number);
    if (idsTexte.some((id) => !/^[1-9]\d*$/.test(id)) || ids.some((id) => !Number.isSafeInteger(id))) {
      this.erreur.set('Saisissez des identifiants de chambre positifs, séparés par des virgules.');
      return;
    }

    this.enregistrement.set(true);
    this.erreur.set(null);
    this.succes.set(null);
    this.blocService.affecterChambres(this.form.controls.idBloc.value, [...new Set(ids)])
      .pipe(finalize(() => this.enregistrement.set(false)))
      .subscribe({
        next: (bloc) => {
          this.succes.set(`Les chambres ont été affectées au bloc « ${bloc.nomBloc} ».`);
          this.form.controls.chambres.reset('');
        },
        error: (error: unknown) => {
          this.erreur.set(messageErreur(error, 'Affectation des chambres'));
        },
      });
  }

  private chargerBlocs(): void {
    this.chargement.set(true);
    this.blocService.getAll()
      .pipe(finalize(() => this.chargement.set(false)))
      .subscribe({
        next: (blocs) => this.blocs.set(blocs),
        error: (error: unknown) => {
          this.erreur.set(messageErreur(error, 'Chargement des blocs'));
        },
      });
  }
}
