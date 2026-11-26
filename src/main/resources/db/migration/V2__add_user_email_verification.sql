-- Execute once on an existing MySQL database before starting the application.
USE makeup;
ALTER TABLE app_user ADD COLUMN email VARCHAR(255) NULL;
ALTER TABLE app_user ADD COLUMN verification_code VARCHAR(255) NULL;
CREATE UNIQUE INDEX ux_app_user_email ON app_user(email);
