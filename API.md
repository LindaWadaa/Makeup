# Documentation de l'API Makeup

## 1. Informations générales

- **URL de base** : `http://localhost:8081`
- **Format** : JSON
- **Authentification** : JWT Bearer pour tous les endpoints protégés
- **CORS autorisé** : `http://localhost:4200`, `http://127.0.0.1:4200`,
  `http://localhost:3000` et `http://127.0.0.1:3000`

Sauf indication contraire, il faut envoyer le token dans l'en-tête :

```http
Authorization: Bearer <token>
```

Les endpoints `OPTIONS` sont autorisés pour permettre les requêtes CORS.

## 2. Authentification

### Créer un utilisateur

```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json
```

Corps :

```json
{
  "username": "admin",
  "password": "123"
}
```

**Fonction** : Créer compte  
**Rôle requis** : Public

Le compte créé reçoit le rôle `USER`. Le mot de passe est enregistré avec BCrypt.

Réponses principales :

- `201 Created` : utilisateur créé
- `400 Bad Request` : `username` ou `password` manquant ou vide
- `409 Conflict` : nom d'utilisateur déjà utilisé

### Se connecter

```http
POST http://localhost:8081/login
Content-Type: application/json
```

Corps :

```json
{
  "username": "admin",
  "password": "123"
}
```

**Fonction** : Obtenir token  
**Rôle requis** : Public

Réponse `200 OK` :

```json
{
  "token": "<jwt>",
  "type": "Bearer"
}
```

Le token est également retourné dans l'en-tête `Authorization`. En cas
d'identifiants invalides, la réponse est `401 Unauthorized`.

Comptes créés au démarrage par l'application :

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `admin` | `123` | `ADMIN`, `USER` |
| `linda` | `123` | `USER` |

## 3. Services de maquillage

Toutes les routes sont préfixées par `http://localhost:8081/api`. Elles
nécessitent un token JWT, sauf `GET /api/health`.

| Méthode | URL complète | Fonction | Rôle requis | Méthode Java | Service |
|---|---|---|---|---|---|
| `GET` | `http://localhost:8081/api/health` | Vérifier serveur | Public | `health()` | — |
| `GET` | `http://localhost:8081/api/allmakeups` | Lister maquillages | `ADMIN` ou `USER` | `getAllMakeups()` | `MakeupServiceImpl.getAllMakeups()` |
| `GET` | `http://localhost:8081/api/getmakeup/{id}` | Détail maquillage | `ADMIN` ou `USER` | `getMakeupById()` | `MakeupServiceImpl.getMakeup()` |
| `GET` | `http://localhost:8081/api/getmakeupbyname/{nom}` | Rechercher nom | `ADMIN` ou `USER` | `findByNomMakeupContains()` | `MakeupServiceImpl.findByNomMakeupContains()` |
| `GET` | `http://localhost:8081/api/getmakeupbymarque/{idMarque}` | Filtrer marque | `ADMIN` ou `USER` | `getMakeupsByMarqueId()` | `MakeupServiceImpl.findByMarqueIdMarque()` |
| `POST` | `http://localhost:8081/api/addmakeup` | Ajouter maquillage | `ADMIN` | `createMakeup()` | `MakeupServiceImpl.saveMakeup()` |
| `PUT` | `http://localhost:8081/api/updatemakeup` | Modifier maquillage | `ADMIN` | `updateMakeup()` | `MakeupServiceImpl.updateMakeup()` |
| `DELETE` | `http://localhost:8081/api/deletemakeup/{id}` | Supprimer maquillage | `ADMIN` | `deleteMakeup()` | `MakeupServiceImpl.deleteMakeupById()` |

> `GET /api/getmakeup/{id}` utilise `orElseThrow()` : un identifiant inexistant
> renvoie une erreur `500`. Utiliser un identifiant réellement présent en base.

### Format d'un maquillage

```json
{
  "id": 1,
  "nom": "Rouge à lèvres",
  "price": 25.5,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "image": { "idImage": 1, "name": "rouge.jpg", "type": "image/jpeg" },
  "images": [],
  "imagePath": "1.jpg",
  "marque": {
    "id": 2,
    "nom": "Exemple",
    "description": "Marque de maquillage"
  }
}
```

Pour une création, l'`id` est généré par la base de données. Le champ
`dateExpiration` utilise le format ISO `YYYY-MM-DD`.

### Ajouter un maquillage

```http
POST http://localhost:8081/api/addmakeup
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "nom": "Rouge à lèvres",
  "price": 25.5,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "marque": {
    "id": 2
  }
}
```

### Modifier un maquillage

```http
PUT http://localhost:8081/api/updatemakeup
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "id": 1,
  "nom": "Rouge à lèvres",
  "price": 29.9,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "marque": {
    "id": 2
  }
}
```

`GET /api/health` retourne simplement :

```text
Makeup API is running
```

## 4. Services de marques

Toutes les routes sont préfixées par `http://localhost:8081/api/marque` et
nécessitent un token JWT.

| Méthode | URL complète | Fonction | Rôle requis | Méthode Java | Repository |
|---|---|---|---|---|---|
| `GET` | `http://localhost:8081/api/marque/all` | Lister marques | `ADMIN` ou `USER` | `getAllMarques()` | `MarqueRepository.findAll()` |
| `GET` | `http://localhost:8081/api/marque/get/{id}` | Détail marque | `ADMIN` ou `USER` | `getMarqueById()` | `MarqueRepository.findById()` |
| `POST` | `http://localhost:8081/api/marque/add` | Ajouter marque | `ADMIN` | `ajouterMarque()` | `MarqueRepository.save()` |
| `PUT` | `http://localhost:8081/api/marque/update` | Modifier marque | `ADMIN` | `modifierMarque()` | `MarqueRepository.save()` |
| `DELETE` | `http://localhost:8081/api/marque/delete/{id}` | Supprimer marque | `ADMIN` | `supprimerMarque()` | `MarqueRepository.deleteById()` |

### Format d'une marque

```json
{
  "id": 2,
  "nom": "Exemple",
  "description": "Marque de maquillage"
}
```

Lors d'une création, l'`id` envoyé est ignoré et un nouvel identifiant est
généré.

Réponses particulières :

- `GET`, `PUT` ou `DELETE` sur une marque inexistante : `404 Not Found`
- `DELETE` réussi : `204 No Content`

## 5. Services d'images (TP Gestion des images)

Routes préfixées par `http://localhost:8081/api/image`. Actuellement `permitAll` dans la config de sécurité pour faciliter les tests.

| Méthode | URL complète | Fonction | Rôle requis | Méthode Java |
|---|---|---|---:|---|
| `POST` | `http://localhost:8081/api/image/upload` | Upload image (BDD) | Public (config actuelle) | `uploadImage()` |
| `GET` | `http://localhost:8081/api/image/get/info/{id}` | Détails image (id, name, type, bytes base64) | Public | `getImageDetails()` |
| `GET` | `http://localhost:8081/api/image/load/{id}` | Charger bytes image | Public | `getImage()` |
| `DELETE` | `http://localhost:8081/api/image/delete/{id}` | Supprimer image BDD | Public | `deleteImage()` |
| `POST` | `http://localhost:8081/api/image/uplaodImageProd/{idProd}` | Upload image liée à un makeup | Public | `uploadMultiImages()` |
| `GET` | `http://localhost:8081/api/image/getImagesProd/{idProd}` | Lister images d'un makeup | Public | `getImagesProd()` |
| `POST` | `http://localhost:8081/api/image/uploadFS/{id}` | Upload image dans dossier FS (`~/images/{id}.jpg`) | Public | `uploadImageFS()` |
| `GET` | `http://localhost:8081/api/image/loadfromFS/{id}` | Charger image depuis FS | Public | `getImageFS()` |

### Format Image (réponse `/upload`, `/get/info/{id}`)
```json
{
  "idImage": 1,
  "name": "rouge.jpg",
  "type": "image/jpeg",
  "image": "<bytes/base64 selon sérialisation>"
}
```

## 6. Endpoints Spring Data REST générés automatiquement

Le projet utilise aussi Spring Data REST. Ces routes sont distinctes des
contrôleurs `/api` et sont préfixées par `/makeup`. Les lectures nécessitent
`ADMIN` ou `USER` ; les écritures nécessitent `ADMIN`.

### Maquillages

| Méthode | URL complète | Fonction | Rôle requis |
|---|---|---|---|
| `GET` | `http://localhost:8081/makeup/rest` | Lister maquillages | `ADMIN` ou `USER` |
| `GET` | `http://localhost:8081/makeup/rest/{id}` | Détail maquillage | `ADMIN` ou `USER` |
| `POST` | `http://localhost:8081/makeup/rest` | Ajouter maquillage | `ADMIN` |
| `PUT` | `http://localhost:8081/makeup/rest/{id}` | Remplacer maquillage | `ADMIN` |
| `PATCH` | `http://localhost:8081/makeup/rest/{id}` | Modifier maquillage | `ADMIN` |
| `DELETE` | `http://localhost:8081/makeup/rest/{id}` | Supprimer maquillage | `ADMIN` |
| `GET` | `http://localhost:8081/makeup/rest/search/findByMarqueIdMarque?id={id}` | Filtrer marque | `ADMIN` ou `USER` |

La liste est paginée avec les paramètres Spring Data REST habituels, par
exemple `?page=0&size=20&sort=nom,asc`.

### Marques

| Méthode | URL complète | Fonction | Rôle requis |
|---|---|---|---|
| `GET` | `http://localhost:8081/makeup/marque` | Lister marques | `ADMIN` ou `USER` |
| `GET` | `http://localhost:8081/makeup/marque/{id}` | Détail marque | `ADMIN` ou `USER` |
| `POST` | `http://localhost:8081/makeup/marque` | Ajouter marque | `ADMIN` |
| `PUT` | `http://localhost:8081/makeup/marque/{id}` | Remplacer marque | `ADMIN` |
| `PATCH` | `http://localhost:8081/makeup/marque/{id}` | Modifier marque | `ADMIN` |
| `DELETE` | `http://localhost:8081/makeup/marque/{id}` | Supprimer marque | `ADMIN` |

### Utilisateurs et rôles

| Méthode | URL complète | Fonction | Rôle requis |
|---|---|---|---|
| `GET` | `http://localhost:8081/makeup/users` | Lister utilisateurs | `ADMIN` ou `USER` |
| `GET` | `http://localhost:8081/makeup/roles` | Lister rôles | `ADMIN` ou `USER` |

Les routes `/api/marque/*` sont les routes métier recommandées pour manipuler
les marques. Les routes `/makeup/*` sont exposées automatiquement par
Spring Data REST.

## 7. Scénario de test sans erreur

Prérequis : le serveur tourne sur `http://localhost:8081` et MySQL est démarré.

1. **Se connecter en ADMIN**

```http
POST http://localhost:8081/login
Content-Type: application/json

{"username":"admin","password":"123"}
```

Récupérer `token`.

2. **Créer une marque**

```http
POST http://localhost:8081/api/marque/add
Content-Type: application/json
Authorization: Bearer <token>

{"nom":"Marque Test","description":"Marque pour le scenario"}
```

Noter l'`id` retourné (`idMarque`).

3. **Créer un maquillage rattaché à cette marque**

```http
POST http://localhost:8081/api/addmakeup
Content-Type: application/json
Authorization: Bearer <token>

{
  "nom": "Rouge Test",
  "price": 25.5,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "marque": {"id": <idMarque>}
}
```

Noter l'`id` retourné (`idMakeup`).

4. **Lire le maquillage créé**

```http
GET http://localhost:8081/api/getmakeup/<idMakeup>
Authorization: Bearer <token>
```

5. **Rechercher par nom**

```http
GET http://localhost:8081/api/getmakeupbyname/Rouge
Authorization: Bearer <token>
```

6. **Filtrer par marque**

```http
GET http://localhost:8081/api/getmakeupbymarque/<idMarque>
Authorization: Bearer <token>
```

7. **Modifier le maquillage**

```http
PUT http://localhost:8081/api/updatemakeup
Content-Type: application/json
Authorization: Bearer <token>

{
  "id": <idMakeup>,
  "nom": "Rouge Test",
  "price": 29.9,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "marque": {"id": <idMarque>}
}
```

8. **Supprimer le maquillage puis la marque**

```http
DELETE http://localhost:8081/api/deletemakeup/<idMakeup>
Authorization: Bearer <token>
```

```http
DELETE http://localhost:8081/api/marque/delete/<idMarque>
Authorization: Bearer <token>
```

9. **Vérifier les droits USER**

Se connecter avec `linda/123`, puis :

- `GET http://localhost:8081/api/allmakeups` → `200 OK`
- `POST http://localhost:8081/api/addmakeup` → `403 Forbidden`






















## 8. Tests Postman - Gestion des images (TP)

Prérequis : backend redémarré sur `8081`, MySQL démarré, migration `V4__add_makeup_images.sql` appliquée. Prépare 2 images locales (ex. `rouge.jpg`, `palette.png`).

### 8.1 Upload image en BDD
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/image/upload`
- **Auth**: none (ou Bearer token, endpoint public)
- **Body**: `form-data`
  - key `image` (type **File**) = sélectionner `rouge.jpg`
- **Attendu**: `200 OK`, JSON avec `idImage`, `name`, `type`, `image`. Noter `idImage`.

### 8.2 Détails image
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/image/get/info/<idImage>`
- **Attendu**: `200 OK`, mêmes métadonnées + bytes.

### 8.3 Charger image brute
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/image/load/<idImage>`
- **Attendu**: `200 OK`, body binaire image (dans Postman, cliquer “Save Response > Save to a file” pour vérifier).

### 8.4 Créer un makeup avec image (flux TP)
1. Créer marque/makeup ou réutiliser un `idMakeup` existant.
2. Upload image:
   - `POST http://localhost:8081/api/image/upload` (form-data `image`)
   - noter `idImage`.
3. Associer au makeup:
   - **Method**: `PUT`
   - **URL**: `http://localhost:8081/api/updatemakeup`
   - **Headers**: `Authorization: Bearer <token_admin>`, `Content-Type: application/json`
   - **Body**:
```json
{
  "id": <idMakeup>,
  "nom": "Rouge Test",
  "price": 29.9,
  "description": "Rouge longue tenue",
  "dateExpiration": "2027-12-31",
  "marque": {"id": <idMarque>},
  "image": {"idImage": <idImage>}
}
```
- **Attendu**: `200 OK`, makeup retourné avec `image.idImage`.

### 8.5 Modifier l'image d'un makeup
1. Upload nouvelle image via `/api/image/upload` -> `newIdImage`.
2. `PUT /api/updatemakeup` avec `"image":{"idImage": <newIdImage>}`.
3. **Attendu**: makeup mis à jour; ancienne image supprimée de la table `image` (vérifier avec `GET /api/image/get/info/<oldIdImage>` -> erreur/absent selon gestion).

### 8.6 Plusieurs images par makeup
- **Upload image liée**:
  - `POST http://localhost:8081/api/image/uplaodImageProd/<idMakeup>`
  - Body form-data: `image` (File) = `palette.png`
  - Attendu: `200 OK`, Image avec `idImage`.
- **Lister images du makeup**:
  - `GET http://localhost:8081/api/image/getImagesProd/<idMakeup>`
  - Attendu: `200 OK`, tableau d'images.
- **Supprimer une image**:
  - `DELETE http://localhost:8081/api/image/delete/<idImage>`
  - Attendu: `200 OK`/succès sans body; l'image ne doit plus apparaître dans la liste.

### 8.7 Stockage fichier système (FS)
- **Upload FS**:
  - `POST http://localhost:8081/api/image/uploadFS/<idMakeup>`
  - Body form-data: `image` (File) = `rouge.jpg`
  - Attendu: `200 OK`. Fichier créé: `C:\Users\<user>\images\<idMakeup>.jpg` (ou `%USERPROFILE%\images\...`).
- **Lire FS**:
  - `GET http://localhost:8081/api/image/loadfromFS/<idMakeup>`
  - Attendu: `200 OK`, image JPEG.
- **Vérifier imagePath**:
  - `GET http://localhost:8081/api/getmakeup/<idMakeup>` avec token admin/user.
  - Attendu: champ `imagePath` = `<idMakeup>.jpg`.

### 8.8 Scénario Postman complet images
1. `POST /login` admin -> token.
2. `POST /api/marque/add` -> `idMarque`.
3. `POST /api/addmakeup` -> `idMakeup`.
4. `POST /api/image/upload` (file) -> `idImage`.
5. `PUT /api/updatemakeup` avec `image.idImage`.
6. `GET /api/getmakeup/{idMakeup}` -> vérifier `image`.
7. `POST /api/image/uplaodImageProd/{idMakeup}` (autre file).
8. `GET /api/image/getImagesProd/{idMakeup}`.
9. `POST /api/image/uploadFS/{idMakeup}`.
10. `GET /api/image/loadfromFS/{idMakeup}`.
11. `DELETE /api/image/delete/{idImageMulti}`.
12. `DELETE /api/deletemakeup/{idMakeup}` -> vérifie aussi suppression du fichier `%USERPROFILE%\images\{idMakeup}.jpg`.

## 9. Codes HTTP courants

| Code | Signification |
|---|---|
| `200 OK` | Requête réussie |
| `201 Created` | Ressource créée |
| `204 No Content` | Suppression réussie sans contenu |
| `400 Bad Request` | Requête ou données invalides |
| `401 Unauthorized` | Token absent, invalide ou identifiants incorrects |
| `403 Forbidden` | Token valide mais rôle insuffisant |
| `404 Not Found` | Ressource inexistante |
| `409 Conflict` | Conflit, notamment un nom d'utilisateur déjà utilisé |
| `500 Internal Server Error` | Erreur serveur (ex. id makeup inexistant avec `orElseThrow`) |

















## 10. Tests Postman - DTO / ModelMapper (TP)

Prérequis : backend redémarré après changements DTO, token ADMIN.

### 10.1 Vérifier DTO sur liste
- **Method**: GET
- **URL**: http://localhost:8081/api/allmakeups
- **Headers**: Authorization: Bearer <token>
- **Attendu**: 200 OK, tableau d'objets MakeupDTO. Chaque objet contient au minimum id, nom, price, description, dateExpiration, marque, nomMarque. Avec MatchingStrategies.LOOSE,
omMarque doit correspondre à marque.nom (ex. "nomMarque":"Dior").

### 10.2 Vérifier DTO sur détail
- **Method**: GET
- **URL**: http://localhost:8081/api/getmakeup/<idMakeup>
- **Headers**: Authorization: Bearer <token>
- **Attendu**: 200 OK, un seul objet DTO avec
omMarque renseigné.

### 10.3 Créer un makeup via DTO
- **Method**: POST
- **URL**: http://localhost:8081/api/addmakeup
- **Headers**: Authorization: Bearer <token_admin>, Content-Type: application/json
- **Body**:
`json
{
  "nom": "Gloss DTO",
  "price": 19.9,
  "description": "Test DTO",
  "dateExpiration": "2027-12-31",
  "marque": { "id": 1 }
}
`
- **Attendu**: 200 OK (ou 201 selon implémentation), corps = MakeupDTO créé avec id généré, marque renseignée,
omMarque correspondant à la marque.

### 10.4 Modifier un makeup via DTO
- **Method**: PUT
- **URL**: http://localhost:8081/api/updatemakeup
- **Headers**: Authorization: Bearer <token_admin>, Content-Type: application/json
- **Body**:
`json
{
  "id": <idMakeup>,
  "nom": "Gloss DTO Modifié",
  "price": 21.9,
  "description": "Test DTO update",
  "dateExpiration": "2027-12-31",
  "marque": { "id": 1 }
}
`
- **Attendu**: 200 OK, DTO retourné avec les nouvelles valeurs et
omMarque correct.

### 10.5 Vérifier conversion ModelMapper
- **Test**: comparer GET /api/getmakeup/{id} avant/après update.
- **Attendu**: les champs DTO sont mappés depuis l'entité Makeup;
omMarque provient du nom de la marque liée (LOOSE matching).
- **Cas négatif**: POST /api/addmakeup avec USER (linda/123) -> 403 Forbidden.
