import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Bloc, BlocPayload, BlocUpdatePayload } from '../models/bloc';

@Injectable({ providedIn: 'root' })
export class BlocService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/blocs`;

  getAll(): Observable<Bloc[]> {
    return this.http.get<Bloc[]>(`${this.baseUrl}/getAll`);
  }

  add(payload: BlocPayload): Observable<Bloc> {
    // The backend DTO needs only these fields; never send a fake foyer.
    return this.http.post<Bloc>(`${this.baseUrl}/add`, payload);
  }

  update(payload: BlocUpdatePayload): Observable<Bloc> {
    return this.http.put<Bloc>(`${this.baseUrl}/update`, payload);
  }

  delete(idBloc: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${idBloc}`);
  }

  affecterChambres(idBloc: number, chambreIds: number[]): Observable<Bloc> {
    const params = new HttpParams().set('idBloc', idBloc);
    return this.http.put<Bloc>(
      `${this.baseUrl}/affecter-chambre-bloc`,
      chambreIds,
      { params },
    );
  }
}
