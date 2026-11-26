-- Execute once on an existing MySQL database before starting the application.
USE makeup;
CREATE TABLE IF NOT EXISTS image (
    id_image BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    image LONGBLOB,
    makeup_id BIGINT,
    CONSTRAINT fk_image_makeup FOREIGN KEY (makeup_id) REFERENCES makeup(id)
);
ALTER TABLE makeup ADD COLUMN image_id BIGINT NULL;
ALTER TABLE makeup ADD COLUMN image_path VARCHAR(255) NULL;
ALTER TABLE makeup ADD CONSTRAINT fk_makeup_image FOREIGN KEY (image_id) REFERENCES image(id_image);
