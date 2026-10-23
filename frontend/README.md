# Frontend tpFoyer — Module Bloc

Application Angular standalone récupérée depuis `feature/gestion-reservation`
et complétée avec une page de gestion des blocs. Les pages existantes des
foyers, universités et réservations restent en place.

## Prérequis

- Node.js `22.22.3` ou plus récent sur la branche Node 22, compatible avec Angular 22.
- npm.
- MySQL local démarré.
- API Spring Boot démarrée sur `http://localhost:8082/tpFoyer17`.

Vérifier les versions dans un terminal Windows :

```cmd
node --version
npm --version
```

## Installer et lancer le projet

Depuis `frontend/` :

```cmd
npm install
npm start
```

Ouvrir `http://localhost:4200`. En développement, `proxy.conf.json` transmet
les appels `/tpFoyer17/**` à `http://localhost:8082`; les appels passent donc
par le même serveur d'origine que l'interface, sans configuration CORS locale.

Démarrer le backend avec MySQL déjà lancé, depuis la racine du dépôt :

```cmd
mvnw.cmd spring-boot:run
```

## Tests et build Angular

```cmd
npm test -- --watch=false
npm run build
```

## Image Docker

Depuis la racine du dépôt, en utilisant le Dockerfile de `frontend/` :

```cmd
docker build -t tpfoyer-frontend -f frontend/Dockerfile frontend
docker run --rm -p 8080:80 --add-host=foyer-bloc-service:host-gateway tpfoyer-frontend
```

L'image utilise le Dockerfile multi-étapes et Nginx de ce dossier. Pour la
production, Nginx sert Angular et transmet `/tpFoyer17/` au service backend
Kubernetes `foyer-bloc-service:8082`, comme défini dans `service.yaml`. Comme
l'API et l'interface sont alors servies sous
la même origine, CORS n'est pas nécessaire dans cette architecture. Si le
frontend est déployé sur une origine différente, une configuration CORS Spring
minimale sera nécessaire ; elle n'est pas ajoutée sans accord préalable.

En local, `--add-host` permet à Nginx de résoudre `foyer-bloc-service` vers la
machine hôte ; le backend doit alors écouter sur le port `8082`. Dans
Kubernetes, le service est résolu par le DNS du cluster.

## Routes de l'API Bloc utilisées

Préfixe observé dans `BlocController` et `application.properties` :
`/tpFoyer17/api/blocs`.

| Fonctionnalité | Méthode | Route |
| --- | --- | --- |
| Lister | GET | `/getAll` |
| Ajouter | POST | `/add` |
| Lire un bloc | GET | `/get?idBloc={idBloc}` (route existante, non nécessaire à l'écran de liste) |
| Modifier | PUT | `/update` |
| Supprimer | DELETE | `/delete/{idBloc}` |
| Affecter des chambres | PUT | `/affecter-chambre-bloc?idBloc={idBloc}` |

Les requêtes d'ajout envoient uniquement `nomBloc` et `capaciteBloc`; les
modifications envoient aussi `idBloc`. Aucun objet `foyer` fictif n'est envoyé.
Pour l'affectation, l'API existante reçoit un tableau d'identifiants de chambres
dans le corps et l'identifiant du bloc dans le paramètre `idBloc`. Les
identifiants sont saisis dans le formulaire, sans inventer d'endpoint de liste
des chambres pour le module Bloc.
