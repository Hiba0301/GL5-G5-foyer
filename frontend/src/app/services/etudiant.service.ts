import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Etudiant } from '../models/etudiant';

@Injectable({ providedIn: 'root' })
export class EtudiantService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/etudiants`;

  getAll(): Observable<Etudiant[]> {
    return this.http.get<Etudiant[]>(`${this.baseUrl}/getAll`);
  }

  getById(idEtudiant: number): Observable<Etudiant> {
    const params = new HttpParams().set('idEtudiant', idEtudiant);
    return this.http.get<Etudiant>(`${this.baseUrl}/get`, { params });
  }

  add(etudiant: Etudiant): Observable<Etudiant> {
    return this.http.post<Etudiant>(`${this.baseUrl}/add`, etudiant);
  }

  update(etudiant: Etudiant): Observable<Etudiant> {
    return this.http.put<Etudiant>(`${this.baseUrl}/update`, etudiant);
  }

  delete(idEtudiant: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${idEtudiant}`);
  }
}
