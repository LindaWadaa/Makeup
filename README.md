# Makeup

Backend Spring Boot de l'application `makeup`, pret a etre connecte a MySQL via XAMPP et a un framework frontend.

## Prerequis

- Java 17 ou plus recent
- Maven 3.9 ou plus recent
- XAMPP avec MySQL demarre pour la persistance

## Lancer le projet

```bash
mvn spring-boot:run
```

Le serveur demarre sur `http://localhost:8081`.

Pour afficher tous les maquillages, utiliser `http://localhost:8081/api/allmakeups`
avec un token JWT. Le frontend peut utiliser `http://localhost:8081/api` comme
URL de base.
Les origines frontend Angular (`localhost:4200`) et React (`localhost:3000`)
sont autorisees par la configuration CORS.

## Connecter MySQL XAMPP

1. Demarrer MySQL dans XAMPP.
2. Creer une base nommee `makeup` dans phpMyAdmin. L'URL configuree peut aussi la creer automatiquement.
3. Adapter le mot de passe MySQL si le compte `root` en utilise un.

Les valeurs peuvent etre surchargees avec `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`.

Les endpoints REST pourront ensuite etre ajoutes dans `src/main/java/com/makeup` et le frontend pourra consommer l'API sur le port `8081`.

## Migration categorie vers marque

Pour une base existante, démarrer une première fois l'application afin de créer
la table `marque`, puis exécuter une seule fois
`src/main/resources/db/migration/V1__rename_categorie_to_marque.sql` dans MySQL.
La table `categorie` devient `marque` et la colonne `makeup.categorie_id`
devient `makeup.marque_id`.

Les marques sont accessibles via `/api/marque/all` et les maquillages d'une
marque via `/api/getmakeupbymarque/{idMarque}`.

## Authentification JWT

L'API utilise le flux du TP avec Spring Security, BCrypt et JWT. Les entités
`User` et `Role` sont séparées des entités métier `Makeup` et `Marque`.

1. Créer un utilisateur :

```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{"username":"admin","password":"123"}
```

2. Se connecter pour obtenir le token :

```http
POST http://localhost:8081/login
Content-Type: application/json

{"username":"admin","password":"123"}
```

3. Envoyer le token reçu avec chaque endpoint protégé :

```http
Authorization: Bearer <token>
```

La clé JWT est configurable avec `JWT_SECRET` et sa durée avec
`JWT_EXPIRATION_MS`. La clé de développement par défaut ne doit pas être
utilisée en production.