import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation } from '../models/reservation';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/reservations`;

  /** Crée une réservation pour l'étudiant (cin) dans le bloc donné. */
  ajouterReservation(idBloc: number, cin: number): Observable<Reservation> {
    const params = new HttpParams().set('idBloc', idBloc).set('cinEtudiant', cin);
    return this.http.put<Reservation>(`${this.baseUrl}/ajouter-reservation`, null, { params });
  }
}
