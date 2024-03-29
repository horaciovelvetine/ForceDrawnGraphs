DROP TABLE IF EXISTS vertices;

CREATE TABLE vertices (
    id SERIAL PRIMARY KEY,
    x DOUBLE,
    y DOUBLE,
    z DOUBLE,
    label VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    views int,
    src_item_id VARCHAR(255),
    src_page_id VARCHAR(255),
)