# Rapport Technique - MesMakeup (Backend + Frontend)

## 1. Vue d'ensemble
Ce projet est une application full stack de gestion de maquillages ("MesMakeup") composée de :
- **Backend**: API REST Spring Boot (Java 17, Maven) avec sécurité JWT, gestion utilisateurs/rôles, vérification email par code, et CRUD Makeup/Marque.
- **Frontend**: application Angular 20 (standalone components) consommant l'API, avec login/register/verification email, gestion des makeups et marques.

Répertoires :
- Backend : `C:\soa`
- Frontend : `C:\soaFront\MesMakeup`

## 2. Outils et technologies utilisés

### Backend (`C:\soa/pom.xml`)
| Outil/Lib | Version | Rôle |
|---|---:|---|
| Java | 17 | Langage/runtime |
| Spring Boot | 3.5.6 | Framework principal |
| spring-boot-starter-web | Boot | API REST / Tomcat |
| spring-boot-starter-data-jpa | Boot | Persistance JPA/Hibernate |
| spring-boot-starter-security | Boot | Authentification/autorisation |
| spring-boot-starter-mail | Boot | Envoi d'emails SMTP |
| spring-boot-starter-data-rest | Boot | Exposition REST repositories (`/makeup`) |
| com.auth0:java-jwt | 3.4.1 | Génération/validation JWT |
| MySQL Connector/J | runtime | Driver MySQL |
| Lombok | optional | Réduction boilerplate (entities/DTOs) |
| spring-boot-devtools | runtime | Rechargement dev |
| Maven | 3.9.x | Build/dependances |

### Frontend (`C:\soaFront/MesMakeup/package.json`)
| Outil/Lib | Version | Rôle |
|---|---:|---|
| Angular | ^20.3.x | Framework SPA |
| TypeScript | ~5.9.2 | Langage |
| RxJS | ~7.8.0 | Programmation réactive/HTTP |
| Angular Router | ^20.3.0 | Navigation |
| Angular Forms | ^20.3.0 | Reactive/Template forms |
| @auth0/angular-jwt | ^5.2.0 | Décodage/expiration JWT côté client |
| Bootstrap | ^5.3.3 | UI/CSS |
| ngx-toastr | ^19.1.0 | Notifications toast |
| Karma/Jasmine | dev | Tests unitaires |

### Infrastructure / Base de données
- **MySQL (XAMPP)**, base `makeup`.
- Port backend par défaut : `8081` (`server.port=${SERVER_PORT:8081}`).
- SMTP Gmail (mot de passe d'application) pour envoi des codes de vérification.

## 3. Configuration et exécution

### Backend
Fichier : `src/main/resources/application.properties`
- DB : `spring.datasource.url=jdbc:mysql://localhost:3306/makeup?...` (XAMPP, user root par défaut)
- JPA : `ddl-auto=none`, `show-sql=true`
- JWT : `app.security.jwt-secret`, `app.security.jwt-expiration-ms`
- Mail : `spring.mail.host=smtp.gmail.com`, `spring.mail.port=587`, `spring.mail.username`, `spring.mail.password`, `app.mail.from`

Lancement :
```powershell
cd C:\soa
$env:MAIL_USERNAME="compte.expediteur@gmail.com"
$env:MAIL_PASSWORD="mot_de_passe_application"
$env:MAIL_FROM="compte.expediteur@gmail.com"
.\.tools\apache-maven-3.9.10\bin\mvn.cmd spring-boot:run
```

### Frontend
Fichiers env : `src/environments/environment.ts`, `environment.development.ts`
- `baseURL: http://localhost:8081`
- `apiURL: http://localhost:8081/api`

Lancement :
```powershell
cd C:\soaFront\MesMakeup
npm install
npm start
```

## 4. Structure du backend (où se trouve quoi)

### 4.1 Entrée et configuration
- `src/main/java/com/makeup/MakeupApplication.java` : classe main Spring Boot.
- `src/main/resources/application.properties` : DB, port, JWT, mail, base path REST.
- `src/main/resources/db/migration/` : scripts SQL manuels.
  - `V1__rename_categorie_to_marque.sql` : migration categorie->marque.
  - `V2__add_user_email_verification.sql` : ajout email/verification_code sur `app_user`.
  - `V3__create_verification_token.sql` : table `verification_token`.

### 4.2 Entités (modèle de données)
- `entities/User.java` : utilisateur (`username` unique, `password` BCrypt, `email`, `verificationCode`, `enabled`, rôles ManyToMany). Table `app_user`.
- `entities/Role.java` : rôle (`USER`, `ADMIN`).
- `entities/VerificationToken.java` : token/code de vérification, `expirationTime` (+15 min), lien `OneToOne` vers `User`. Table `verification_token`.
- `entities/Makeup.java` : produit makeup (nom, prix, dateCreation, relation `Marque`).
- `entities/Marque.java` : marque/catégorie.
- `entities/MakeupProjection.java` : projection Spring Data REST.

### 4.3 Repositories
- `repos/UserRepository.java` : `findByUsername`, `findByEmail`, `existsByUsername/Email`.
- `repos/RoleRepository.java` : `findByRole`.
- `repos/VerificationTokenRepository.java` : `findByToken`.
- `repos/MakeupRepository.java`, `repos/MarqueRepository.java` : accès données métier.

### 4.4 Sécurité (JWT + Spring Security)
- `security/SecurityConfig.java` : configuration centrale.
  - CSRF désactivé, session `STATELESS`, CORS autorisé pour `localhost:4200/3000`.
  - Routes publiques : `/login`, `/register/**`, `/verifyEmail/**`, `/api/auth/*`, `/api/health`, `OPTIONS`, `/error`.
  - Règles : GET makeup/marque pour `ADMIN,USER`; POST/PUT/DELETE makeup/marque pour `ADMIN`.
  - Ajoute `JwtAuthenticationFilter` (login) et `JwtAuthorizationFilter` (Bearer token).
- `security/SecurityBeansConfiguration.java` : beans `AuthenticationManager`, `BCryptPasswordEncoder`.
- `security/JwtService.java` : génération/validation token JWT (secret, expiration, claim `roles`).
- `security/JwtAuthenticationFilter.java` : traite `POST /login`, retourne token; si compte désactivé renvoie 403 JSON `{errorCause:"disabled", ...}`.
- `security/JwtAuthorizationFilter.java` : lit header `Authorization: Bearer ...`, valide JWT, met l'authentification dans le SecurityContext.
- `security/MyUserDetailsService.java` : charge `User` depuis DB, transmet `enabled` à Spring Security.

### 4.5 Services métier
- `service/UserService.java` / `UserServiceImpl.java` :
  - `registerUser(RegistationRequest)` : vérifie doublon email/username, crée user désactivé, encode mot de passe, assigne rôle `USER`, génère code, sauvegarde `VerificationToken`, envoie email.
  - `validateToken(code)` : vérifie token, expiration, active l'utilisateur.
  - `generateCode()` : code 6 chiffres (SecureRandom).
  - `sendEmailUser()` : construit email HTML et délègue à `EmailSender`.
- `service/EmailService.java` : implémente `EmailSender`, envoie SMTP via `JavaMailSender/MimeMessageHelper`. Contient aussi compat `generateCode/sendVerificationCode`.
- `util/EmailSender.java` : interface d'envoi email.
- `service/MakeupService*.java` : logique CRUD/recherche makeup.

### 4.6 Contrôleurs REST
- `RestControllers/UserRestController.java` (flux TP principal) :
  - `POST /register` : inscription.
  - `GET /verifyEmail/{token}` : validation email.
- `RestControllers/AuthController.java` (`/api/auth`) : endpoints alternatifs/compat (`/register`, `/verifyEmail`, `/resendCode`) utilisant `verificationCode` sur `User`.
- `RestControllers/MakeupRestController.java` : endpoints makeup sous `/api` (`/allmakeups`, `/addmakeup`, `/updatemakeup`, `/deletemakeup/{id}`, recherches).
- `RestControllers/MarqueRESTController.java` : CRUD marque sous `/api/marque`.

### 4.7 Gestion des erreurs
- `exception/ErrorDetails.java` : format erreur (`timestamp`, `message`, `path`, `errorCode`).
- `exception/EmailAlreadyExistsException.java`, `UsernameAlreadyExistsException.java`, `InvalidTokenException.java`, `ExpiredTokenException.java`.
- `exception/GlobalExceptionHandler.java` : mappe exceptions vers HTTP/JSON :
  - email existant -> 400 `USER_EMAIL_ALREADY_EXISTS`
  - username existant -> 400 `USERNAME_ALREADY_EXISTS`
  - token invalide -> 404 `INVALID_TOKEN`
  - token expiré -> 400 `EXPIRED_TOKEN`
  - autre -> 500 `INTERNAL SERVER ERROR`

## 5. Structure du frontend (où se trouve quoi)

### 5.1 Configuration
- `src/app/app.config.ts` : providers Router, HttpClient + interceptor, animations, toastr.
- `src/app/app.routes.ts` : routes (`/login`, `/register`, `/verifEmail`, `/makeups`, admin routes, etc.).
- `src/app/services/token-interceptor.ts` : ajoute `Authorization: Bearer <jwt>` sauf pour `/login,/register,/verifyEmail`.
- `src/environments/environment*.ts` : URLs backend.

### 5.2 Modèles
- `model/user.model.ts` : `username,email,password,enabled,roles`.
- `model/makeup.model.ts`, `marque.model.ts`, `marque-wrapped.model.ts`.

### 5.3 Services Angular
- `services/auth.ts` (`AuthService`) :
  - `login()` -> `POST /login`, stocke JWT.
  - `registerUser()` -> `POST /register`.
  - `validateEmail(code)` -> `GET /verifyEmail/{code}`.
  - gestion token localStorage (`jwt`), décodage JWT, rôles, expiration, logout.
  - contient aussi méthodes legacy `/api/auth/*` (`register/verifEmail/resendCode`).
- `services/makeup.ts` : appels CRUD makeup/marque.

### 5.4 Composants liés au TP inscription
- `register/register.ts` + `.html` : ReactiveForm (username, email, password, confirmPassword), validations, spinner, toastr, appelle `registerUser`, stocke user, redirige `/verifEmail`.
- `verif-email/verif-email.ts` + `.html` : saisie code, appelle `validateEmail`, puis auto-login avec user enregistré, stocke JWT, redirige; gère `INVALID_TOKEN`/`EXPIRED_TOKEN`.
- `login/login.ts` + `.html` : login, affiche erreur disabled via `errorCause=='disabled'`, lien vers register.
- `guards/admin.guard.ts` : protection routes admin.

### 5.5 Composants métier makeup
- `makeups/`, `add-makeup/`, `update-makeup/` : CRUD makeup.
- `liste-marques/`, `update-marque/` : gestion marques.
- `recherche-par-marque/`, `recherche-par-nom/` : recherche.
- `profile/`, `forbidden/` : pages utilitaires.

## 6. Fonctionnalités - flux détaillé

### 6.1 Inscription (TP)
1. Front `Register` valide le formulaire et POST `/register`.
2. `UserRestController.register` appelle `UserServiceImpl.registerUser`.
3. Backend vérifie email/username uniques, crée `User` `enabled=false`, encode password BCrypt, ajoute rôle `USER`.
4. Génère code 6 chiffres, crée `VerificationToken` (expiration +15 min), sauvegarde.
5. `EmailService.sendEmail` envoie HTML via SMTP Gmail.
6. Front affiche toastr et navigue vers `/verifEmail`.

### 6.2 Vérification email
1. Front `VerifEmail` envoie `GET /verifyEmail/{code}`.
2. Backend cherche token, vérifie existence/expiration.
3. Si valide : `user.enabled=true`, sauvegarde, retourne user.
4. Front effectue auto-login puis redirige.

### 6.3 Login JWT
1. Front POST `/login` avec `{username,password}`.
2. `JwtAuthenticationFilter` authentifie via `AuthenticationManager`.
3. `MyUserDetailsService` refuse implicitement les users disabled (flag `enabled`).
4. Succès : JWT dans header `Authorization` et body. Échec disabled : 403 + `errorCause`.

### 6.4 Autorisation
- JWT contient `sub` (username) et `roles`.
- `JwtAuthorizationFilter` valide token sur requêtes protégées.
- `SecurityConfig` applique règles par endpoint/méthode HTTP.

### 6.5 CRUD Makeup/Marque
- API `/api/*` pour makeup/marque; opérations d'écriture réservées ADMIN.
- Spring Data REST exposé aussi sous `/makeup` (selon config).

## 7. Sécurité et points d'attention
- **Secrets** : ne pas committer `MAIL_PASSWORD`, `JWT_SECRET`. Utiliser variables d'environnement.
- **Mot de passe Gmail** : utiliser un mot de passe d'application; révoquer si exposé.
- **Token verification** : expiration 15 min; après validation réussie, envisager suppression du token pour hygiène.
- **CORS** : origines autorisées limitées à localhost dev.
- **DDL** : `spring.jpa.hibernate.ddl-auto=none`; les migrations SQL doivent être appliquées manuellement sur base existante.
- **Compte expéditeur mail** : un seul compte Gmail expéditeur suffit; les destinataires n'ont aucune configuration à faire.

## 8. Cartographie rapide "fonctionnalité -> fichiers"

| Fonctionnalité | Backend | Frontend |
|---|---|---|
| Inscription | `UserRestController`, `UserServiceImpl.registerUser`, `RegistationRequest`, `VerificationToken`, `EmailService` | `register/register.ts/.html`, `services/auth.ts` |
| Vérification email | `UserRestController.verifyEmail`, `UserServiceImpl.validateToken`, `GlobalExceptionHandler` | `verif-email/verif-email.ts/.html` |
| Login JWT | `JwtAuthenticationFilter`, `JwtService`, `MyUserDetailsService`, `SecurityConfig` | `login/login.ts/.html`, `services/auth.ts` |
| Interception token | `JwtAuthorizationFilter` | `services/token-interceptor.ts` |
| Gestion erreurs | `exception/*` | gestion `err.error.errorCode/errorCause` dans composants |
| Makeup CRUD | `MakeupRestController`, `MakeupService*`, `MakeupRepository` | `makeups/*`, `add-makeup/*`, `update-makeup/*`, `services/makeup.ts` |
| Marque CRUD | `MarqueRESTController`, `MarqueRepository` | `liste-marques/*`, `update-marque/*` |

## 9. Commandes utiles
```powershell
# Backend - compiler
cd C:\soa
.\.tools\apache-maven-3.9.10\bin\mvn.cmd -DskipTests compile

# Backend - lancer
.\.tools\apache-maven-3.9.10\bin\mvn.cmd spring-boot:run

# Frontend - lancer
cd C:\soaFront\MesMakeup
npm start

# Frontend - build
npm run build
```
