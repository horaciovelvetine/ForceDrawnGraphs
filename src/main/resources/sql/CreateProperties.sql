DROP TABLE IF EXISTS properties;

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    property_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    number_of_references INT DEFAULT 0
);

CREATE INDEX idx_property_id ON properties (property_id);