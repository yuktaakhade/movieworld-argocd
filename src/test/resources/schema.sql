-- Test schema for H2 database
CREATE TABLE IF NOT EXISTS movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    director VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    duration_minutes INT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    description TEXT,
    image_path VARCHAR(255)
);
