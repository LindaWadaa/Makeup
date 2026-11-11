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

## 5. Endpoints Spring Data REST générés automatiquement

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

## 6. Scénario de test sans erreur

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

## 7. Codes HTTP courants

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
