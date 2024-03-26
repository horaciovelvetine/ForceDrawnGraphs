DROP TABLE IF EXISTS properties;

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    property_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref INT
);