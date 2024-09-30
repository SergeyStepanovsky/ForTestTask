CREATE TABLE settings (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) UNIQUE NOT NULL,
    value VARCHAR(255) NOT NULL
);

INSERT INTO settings (key, value) VALUES ('distanceRatioThreshold', '0.9');
