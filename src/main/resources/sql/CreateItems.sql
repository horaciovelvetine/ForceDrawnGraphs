DROP TABLE IF EXISTS items;

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    item_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT
);