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

## Connecter MySQL XAMPP

1. Demarrer MySQL dans XAMPP.
2. Creer une base nommee `makeup` dans phpMyAdmin. L'URL configuree peut aussi la creer automatiquement.
3. Adapter le mot de passe MySQL si le compte `root` en utilise un.

Les valeurs peuvent etre surchargees avec `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`.

Les endpoints REST pourront ensuite etre ajoutes dans `src/main/java/com/makeup` et le frontend pourra consommer l'API sur le port `8080`.