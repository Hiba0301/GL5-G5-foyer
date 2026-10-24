import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { messageErreur } from '../../shared/http-error';

type TypePage = 'reservations' | 'blocs' | 'chambres' | 'etudiants';

@Component({
  selector: 'app-gestion', imports: [FormsModule], templateUrl: './gestion.html',
})
export class Gestion implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly api = environment.apiUrl;
  protected type: TypePage = 'reservations';
  protected readonly elements = signal<any[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);
  protected readonly edition = signal(false);
  protected readonly form: any = {};
  protected readonly blocs = signal<any[]>([]);
  protected readonly chambresDuBloc = signal<any[]>([]);

  protected readonly titres: Record<TypePage, string> = {
    reservations: 'Réservations', blocs: 'Blocs', chambres: 'Chambres', etudiants: 'Étudiants',
  };
  protected readonly bases: Record<TypePage, string> = {
    reservations: '/api/reservations', blocs: '/api/blocs', chambres: '/api/chambres', etudiants: '/api/etudiants',
  };

  ngOnInit(): void {
    const segment = this.route.snapshot.routeConfig?.path as TypePage;
    this.type = segment in this.bases ? segment : 'reservations';
    this.charger();
    if (this.type === 'reservations') this.chargerBlocs();
  }

  protected charger(): void {
    this.chargement.set(true);
    this.http.get<any[]>(`${this.api}${this.bases[this.type]}/getAll`).subscribe({
      next: (rows) => { this.elements.set(rows); this.chargement.set(false); },
      error: (err) => { this.erreur.set(messageErreur(err, `Chargement des ${this.titres[this.type].toLowerCase()}`)); this.chargement.set(false); },
    });
  }

  private chargerBlocs(): void {
    this.http.get<any[]>(`${this.api}/api/blocs/getAll`).subscribe({
      next: (rows) => this.blocs.set(rows),
      error: (err) => this.erreur.set(messageErreur(err, 'Chargement des blocs')),
    });
  }

  protected onBlocChoisi(): void {
    this.form.idChambre = undefined;
    this.chambresDuBloc.set([]);
    const idBloc = Number(this.form.idBloc);
    if (!idBloc) return;
    this.http.get<any[]>(`${this.api}/api/chambres/chambres-par-bloc`, { params: { idBloc: String(idBloc) } }).subscribe({
      next: (rows) => this.chambresDuBloc.set(rows),
      error: (err) => this.erreur.set(messageErreur(err, 'Chargement des chambres')),
    });
  }

  protected champs(): string[] {
    return ({
      blocs: ['nomBloc', 'capaciteBloc'], chambres: ['numeroChambre', 'typeChambre'],
      etudiants: ['nomEtudiant', 'prenomEtudiant', 'cinEtudiant', 'dateNaissance'], reservations: [],
    } as Record<TypePage, string[]>)[this.type];
  }

  protected commencer(item?: any): void {
    Object.keys(this.form).forEach((key) => delete this.form[key]);
    if (item) Object.assign(this.form, item, { dateNaissance: item.dateNaissance ?? '' });
    else if (this.type === 'chambres') this.form.typeChambre = 'SIMPLE';
    this.chambresDuBloc.set([]);
    this.edition.set(true);
    this.erreur.set(null);
  }

  protected enregistrer(): void {
    const edit = this.type === 'blocs' ? this.form.idBloc != null : this.type === 'chambres' ? this.form.idChambre != null : this.form.idEtudiant != null;
    const dto = this.type === 'blocs' ? { idBloc: this.form.idBloc ?? 0, nomBloc: this.form.nomBloc, capaciteBloc: Number(this.form.capaciteBloc), foyer: this.form.foyer ?? null }
      : this.type === 'chambres' ? { idChambre: this.form.idChambre ?? 0, numeroChambre: Number(this.form.numeroChambre), typeChambre: this.form.typeChambre }
        : { idEtudiant: this.form.idEtudiant ?? 0, nomEtudiant: this.form.nomEtudiant, prenomEtudiant: this.form.prenomEtudiant, cinEtudiant: Number(this.form.cinEtudiant), dateNaissance: this.form.dateNaissance };
    this.http.request('' + (edit ? 'PUT' : 'POST'), `${this.api}${this.bases[this.type]}/${edit ? 'update' : 'add'}`, { body: dto }).subscribe({
      next: () => { this.edition.set(false); this.succes.set('Enregistrement effectué.'); this.charger(); },
      error: (err) => this.erreur.set(messageErreur(err, "Enregistrement")),
    });
  }

  protected ajouterReservation(): void {
    const idChambre = Number(this.form.idChambre);
    const cinEtudiant = Number(this.form.cinEtudiant);
    if (!idChambre || !cinEtudiant) {
      this.erreur.set('Choisissez une chambre et saisissez le CIN de l’étudiant.');
      return;
    }
    this.http.put(`${this.api}/api/reservations/ajouter-reservation`, null, {
      params: { idChambre: String(idChambre), cinEtudiant: String(cinEtudiant) },
    }).subscribe({
      next: () => { this.edition.set(false); this.succes.set('Réservation créée.'); this.charger(); },
      error: (err) => this.erreur.set(messageErreur(err, 'Création de la réservation')),
    });
  }

  protected supprimer(item: any): void {
    const id = this.type === 'reservations' ? item.idReservation : item[this.type === 'blocs' ? 'idBloc' : this.type === 'chambres' ? 'idChambre' : 'idEtudiant'];
    if (!confirm(`Supprimer cet élément ?`)) return;
    this.http.delete(`${this.api}${this.bases[this.type]}/delete/${id}`).subscribe({
      next: () => { this.succes.set('Suppression effectuée.'); this.charger(); },
      error: (err) => this.erreur.set(messageErreur(err, 'Suppression')),
    });
  }
}
