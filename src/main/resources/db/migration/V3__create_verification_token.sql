-- Execute once on an existing MySQL database before starting the application.
USE makeup;
CREATE TABLE IF NOT EXISTS verification_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiration_time DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);
CREATE UNIQUE INDEX ux_verification_token_token ON verification_token(token);
