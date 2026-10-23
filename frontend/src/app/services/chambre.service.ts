import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Chambre, TypeChambre } from '../models/chambre';

@Injectable({ providedIn: 'root' })
export class ChambreService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/tpFoyer17/api/chambres';

  getAll(): Observable<Chambre[]> {
    return this.http.get<Chambre[]>(`${this.baseUrl}/getAll`);
  }

  getById(idChambre: number): Observable<Chambre> {
    const params = new HttpParams().set('idChambre', idChambre);
    return this.http.get<Chambre>(`${this.baseUrl}/get`, { params });
  }

  add(chambre: Chambre): Observable<Chambre> {
    return this.http.post<Chambre>(`${this.baseUrl}/add`, chambre);
  }

  update(chambre: Chambre): Observable<Chambre> {
    return this.http.put<Chambre>(`${this.baseUrl}/update`, chambre);
  }

  delete(idChambre: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${idChambre}`);
  }

  getByNomUniversite(nomUniversite: string): Observable<Chambre[]> {
    const params = new HttpParams().set('nomUniversite', nomUniversite);
    return this.http.get<Chambre[]>(`${this.baseUrl}/get-par-nom-universite`, { params });
  }

  getByBlocAndType(idBloc: number, typeC: TypeChambre): Observable<Chambre[]> {
    const params = new HttpParams().set('idBloc', idBloc).set('typeC', typeC);
    return this.http.get<Chambre[]>(`${this.baseUrl}/get-par-bloc-type-keyword`, { params });
  }

  getByBlocAndTypeJpql(idBloc: number, typeC: TypeChambre): Observable<Chambre[]> {
    const params = new HttpParams().set('idBloc', idBloc).set('typeC', typeC);
    return this.http.get<Chambre[]>(`${this.baseUrl}/get-par-bloc-type-jpql`, { params });
  }

  getNonReserveByNomUniversiteAndType(nomUniversite: string, type: TypeChambre): Observable<Chambre[]> {
    const params = new HttpParams().set('nomUniversite', nomUniversite).set('type', type);
    return this.http.get<Chambre[]>(
      `${this.baseUrl}/get-non-reserve-par-nom-universite-typechambre`,
      { params },
    );
  }
}
