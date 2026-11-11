-- Execute once on an existing MySQL database before starting the application.
USE makeup;
INSERT INTO marque (id, description, nom)
SELECT id, description, nom FROM categorie
ON DUPLICATE KEY UPDATE description = VALUES(description), nom = VALUES(nom);
UPDATE makeup SET marque_id = categorie_id WHERE categorie_id IS NOT NULL AND marque_id IS NULL;
ALTER TABLE makeup DROP FOREIGN KEY FKthtu4bh1ascjc56rdcc8yi4c1;
ALTER TABLE makeup DROP INDEX FKthtu4bh1ascjc56rdcc8yi4c1;
ALTER TABLE makeup DROP COLUMN categorie_id;
ALTER TABLE makeup DROP COLUMN marque;
DROP TABLE categorie;
