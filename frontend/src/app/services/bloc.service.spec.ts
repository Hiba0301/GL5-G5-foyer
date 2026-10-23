import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { BlocService } from './bloc.service';

describe('BlocService', () => {
  let service: BlocService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(BlocService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads blocks from the controller route', () => {
    service.getAll().subscribe((blocs) => {
      expect(blocs).toEqual([{ idBloc: 1, nomBloc: 'Bloc A', capaciteBloc: 30 }]);
    });

    const request = http.expectOne('/tpFoyer17/api/blocs/getAll');
    expect(request.request.method).toBe('GET');
    request.flush([{ idBloc: 1, nomBloc: 'Bloc A', capaciteBloc: 30 }]);
  });

  it('creates a block with only the Bloc DTO fields', () => {
    service.add({ nomBloc: 'Bloc B', capaciteBloc: 20 }).subscribe();

    const request = http.expectOne('/tpFoyer17/api/blocs/add');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ nomBloc: 'Bloc B', capaciteBloc: 20 });
    request.flush({ idBloc: 2, nomBloc: 'Bloc B', capaciteBloc: 20 });
  });

  it('updates and deletes blocks using the existing routes', () => {
    service
      .update({ idBloc: 3, nomBloc: 'Bloc C', capaciteBloc: 12 })
      .subscribe();

    const update = http.expectOne('/tpFoyer17/api/blocs/update');
    expect(update.request.method).toBe('PUT');
    expect(update.request.body).toEqual({
      idBloc: 3,
      nomBloc: 'Bloc C',
      capaciteBloc: 12,
    });
    update.flush({ idBloc: 3, nomBloc: 'Bloc C', capaciteBloc: 12 });

    service.delete(3).subscribe();
    const remove = http.expectOne('/tpFoyer17/api/blocs/delete/3');
    expect(remove.request.method).toBe('DELETE');
    remove.flush(null);
  });

  it('sends chamber IDs and the selected block ID to the assignment route', () => {
    service.affecterChambres(3, [8, 9]).subscribe();

    const request = http.expectOne(
      (candidate) =>
        candidate.url === '/tpFoyer17/api/blocs/affecter-chambre-bloc' &&
        candidate.params.get('idBloc') === '3',
    );
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual([8, 9]);
    request.flush({ idBloc: 3, nomBloc: 'Bloc C', capaciteBloc: 12 });
  });
});
