import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { Chambre, TypeChambre } from '../../models/chambre';
import { ChambreService } from '../../services/chambre.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-chambre-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './chambre-form.html',
})
export class ChambreForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chambreService = inject(ChambreService);

  protected readonly idChambre = signal<number | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);
  protected readonly envoi = signal(false);
  protected readonly types: TypeChambre[] = ['SIMPLE', 'DOUBLE', 'TRIPLE'];

  protected readonly form = this.fb.nonNullable.group({
    numeroChambre: [1, [Validators.required, Validators.min(1), Validators.pattern(/^\d+$/)]],
    typeChambre: ['SIMPLE' as TypeChambre, [Validators.required]],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }

    this.idChambre.set(Number(id));
    this.chambreService.getById(Number(id)).subscribe({
      next: (chambre) =>
        this.form.patchValue({
          numeroChambre: chambre.numeroChambre,
          typeChambre: chambre.typeChambre,
        }),
      error: (err) => this.erreur.set(messageErreur(err, 'Chargement de la chambre')),
    });
  }

  protected champInvalide(nom: 'numeroChambre' | 'typeChambre'): boolean {
    const control = this.form.controls[nom];
    return control.invalid && (control.touched || control.dirty);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valeurs = this.form.getRawValue();
    const chambre: Chambre = {
      numeroChambre: Number(valeurs.numeroChambre),
      typeChambre: valeurs.typeChambre,
    };

    let requete: Observable<Chambre>;
    if (this.idChambre() !== null) {
      requete = this.chambreService.update({ ...chambre, idChambre: this.idChambre()! });
    } else {
      requete = this.chambreService.add(chambre);
    }

    this.envoi.set(true);
    this.erreur.set(null);
    requete.subscribe({
      next: () => {
        this.succes.set('Chambre enregistrée.');
        window.setTimeout(() => this.router.navigate(['/chambres'], { state: { succes: 'Chambre enregistrée.' } }), 3000);
      },
      error: (err) => {
        this.erreur.set(messageErreur(err, 'Enregistrement de la chambre'));
        this.envoi.set(false);
      },
    });
  }
}
