-- ============================================================
-- Crop Pest Predictor - MySQL Database Schema
-- Run ONCE before starting the application:
--   mysql -u root -p < sql/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS pest_predictor
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pest_predictor;

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100),
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login    DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prediction_history (
    id                     BIGINT  NOT NULL AUTO_INCREMENT,
    user_id                BIGINT  NOT NULL,
    temperature            DOUBLE,
    humidity               DOUBLE,
    wind_speed             DOUBLE,
    nitrogen               DOUBLE,
    phosphorus             DOUBLE,
    potassium              DOUBLE,
    organic_matter         DOUBLE,
    soil_moisture          DOUBLE,
    rainfall               DOUBLE,
    irrigation_frequency   DOUBLE,
    water_usage_efficiency DOUBLE,
    sunlight_exposure      DOUBLE,
    co2_concentration      DOUBLE,
    fertilizer_usage       DOUBLE,
    predicted_class        INT,
    predicted_label        VARCHAR(100),
    confidence_score       DOUBLE,
    probabilities          VARCHAR(500),
    notes                  VARCHAR(500),
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ph_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ph_user (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SELECT 'Schema created successfully.' AS status;
