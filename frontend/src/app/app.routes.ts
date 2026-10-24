import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'foyers', pathMatch: 'full' },
  { path: 'reservations', title: 'Réservations', loadComponent: () => import('./pages/gestion/gestion').then((m) => m.Gestion) },
  {
    path: 'blocs',
    title: 'Blocs',
    loadComponent: () => import('./pages/blocs/bloc-list').then((m) => m.BlocList),
  },
  {
    path: 'blocs/affectation',
    title: 'Affecter des chambres',
    loadComponent: () => import('./pages/blocs/bloc-affectation').then((m) => m.BlocAffectation),
  },
  {
    path: 'chambres',
    title: 'Chambres',
    loadComponent: () => import('./pages/chambres/chambre-list').then((m) => m.ChambreList),
  },
  {
    path: 'foyers',
    title: 'Foyers',
    loadComponent: () => import('./pages/foyers/foyer-list').then((m) => m.FoyerList),
  },
  {
    path: 'foyers/nouveau',
    title: 'Ajouter un foyer',
    loadComponent: () => import('./pages/foyers/foyer-form').then((m) => m.FoyerForm),
  },
  {
    path: 'foyers/:id/modifier',
    title: 'Modifier un foyer',
    loadComponent: () => import('./pages/foyers/foyer-form').then((m) => m.FoyerForm),
  },
  {
    path: 'universites',
    title: 'Universités',
    loadComponent: () => import('./pages/universites/universite-list').then((m) => m.UniversiteList),
  },
  {
    path: 'universites/nouveau',
    title: 'Ajouter une université',
    loadComponent: () => import('./pages/universites/universite-form').then((m) => m.UniversiteForm),
  },
  {
    path: 'universites/:id/modifier',
    title: 'Modifier une université',
    loadComponent: () => import('./pages/universites/universite-form').then((m) => m.UniversiteForm),
  },
  {
    path: 'etudiants',
    title: 'Étudiants',
    loadComponent: () => import('./pages/etudiants/etudiant-list').then((m) => m.EtudiantList),
  },
  {
    path: 'etudiants/nouveau',
    title: 'Ajouter un étudiant',
    loadComponent: () => import('./pages/etudiants/etudiant-form').then((m) => m.EtudiantForm),
  },
  {
    path: 'etudiants/:id/modifier',
    title: 'Modifier un étudiant',
    loadComponent: () => import('./pages/etudiants/etudiant-form').then((m) => m.EtudiantForm),
  },
  {
    path: 'chambres/nouveau',
    title: 'Ajouter une chambre',
    loadComponent: () => import('./pages/chambres/chambre-form').then((m) => m.ChambreForm),
  },
  {
    path: 'chambres/:id/modifier',
    title: 'Modifier une chambre',
    loadComponent: () => import('./pages/chambres/chambre-form').then((m) => m.ChambreForm),
  },
  { path: '**', redirectTo: 'foyers' },
];
