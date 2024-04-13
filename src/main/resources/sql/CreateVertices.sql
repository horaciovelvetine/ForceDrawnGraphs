DROP TABLE IF EXISTS vertices;

CREATE TABLE vertices (
    id SERIAL PRIMARY KEY,
    x FLOAT,
    y FLOAT,
    z FLOAT,
    label VARCHAR(255),
    title VARCHAR(255),
    en_description TEXT,
    views VARCHAR(255),
    src_item_id VARCHAR(255),
    src_page_id VARCHAR(255)
);

CREATE INDEX idx_src_item_id ON vertices (src_item_id);
CREATE INDEX idx_src_page_id ON vertices (src_page_id);
