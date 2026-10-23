import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { Etudiant } from '../../models/etudiant';
import { EtudiantService } from '../../services/etudiant.service';
import { messageErreur } from '../../shared/http-error';

@Component({
  selector: 'app-etudiant-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './etudiant-form.html',
})
export class EtudiantForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly etudiantService = inject(EtudiantService);

  protected readonly idEtudiant = signal<number | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly envoi = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    nomEtudiant: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    prenomEtudiant: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    cinEtudiant: [0, [Validators.required, Validators.min(1), Validators.pattern(/^\d+$/)]],
    dateNaissance: [''],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }

    this.idEtudiant.set(Number(id));
    this.etudiantService.getById(Number(id)).subscribe({
      next: (etudiant) => {
        this.form.patchValue({
          nomEtudiant: etudiant.nomEtudiant,
          prenomEtudiant: etudiant.prenomEtudiant,
          cinEtudiant: etudiant.cinEtudiant,
          dateNaissance: this.toInputDate(etudiant.dateNaissance),
        });
      },
      error: (err) => this.erreur.set(messageErreur(err, 'Chargement de l\'étudiant')),
    });
  }

  protected champInvalide(nom: 'nomEtudiant' | 'prenomEtudiant' | 'cinEtudiant'): boolean {
    const control = this.form.controls[nom];
    return control.invalid && (control.touched || control.dirty);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { nomEtudiant, prenomEtudiant, cinEtudiant, dateNaissance } = this.form.getRawValue();
    const etudiant: Etudiant = {
      nomEtudiant: nomEtudiant.trim(),
      prenomEtudiant: prenomEtudiant.trim(),
      cinEtudiant: Number(cinEtudiant),
      dateNaissance: dateNaissance ? this.toApiDate(dateNaissance) : null,
    };

    let requete: Observable<Etudiant>;
    if (this.idEtudiant() !== null) {
      requete = this.etudiantService.update({ ...etudiant, idEtudiant: this.idEtudiant()! });
    } else {
      requete = this.etudiantService.add(etudiant);
    }

    this.envoi.set(true);
    this.erreur.set(null);
    requete.subscribe({
      next: () => this.router.navigate(['/etudiants']),
      error: (err) => {
        this.erreur.set(messageErreur(err, 'Enregistrement de l\'étudiant'));
        this.envoi.set(false);
      },
    });
  }

  private toInputDate(value?: string | null): string {
    if (!value) {
      return '';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    return date.toISOString().slice(0, 10);
  }

  private toApiDate(value: string): string {
    return new Date(`${value}T00:00:00`).toISOString();
  }
}
