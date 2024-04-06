CREATE TABLE edges (
  id SERIAL PRIMARY KEY,
  src_vertex_id INT,
  tgt_vertex_id INT,
  weight VARCHAR(255),
  edge_type VARCHAR(255),
  edge_property_id VARCHAR(255)
);

CREATE INDEX idx_src_vertex_id ON edges (src_vertex_id);
CREATE INDEX idx_tgt_vertex_id ON edges (tgt_vertex_id);

CREATE TABLE hyperlinks (
    id SERIAL PRIMARY KEY,
    from_page_id VARCHAR(255),
    to_page_id VARCHAR(255),
    count VARCHAR(255)
);

CREATE INDEX idx_from_page_id ON hyperlinks (from_page_id);
CREATE INDEX idx_to_page_id ON hyperlinks (to_page_id);

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    item_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT
);

CREATE INDEX idx_item_id ON items (item_id);

CREATE TABLE pages (
    id SERIAL PRIMARY KEY,
    page_id VARCHAR(255),
    item_id VARCHAR(255),
    title VARCHAR(255),
    views VARCHAR(255)
);

CREATE INDEX idx_page_id ON pages (page_id);
CREATE INDEX idx_page_item_id ON pages (item_id);

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    property_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT
);

CREATE TABLE statements (
    id SERIAL PRIMARY KEY,
    source_item_id VARCHAR(255),
    edge_property_id VARCHAR(255),
    target_item_id VARCHAR(255)
);

CREATE INDEX idx_source_item_id ON statements (source_item_id);
CREATE INDEX idx_edge_property_id ON statements (edge_property_id);
CREATE INDEX idx_target_item_id ON statements (target_item_id);

CREATE TABLE vertices (
    id SERIAL PRIMARY KEY,
    x FLOAT,
    y FLOAT,
    z FLOAT,
    label VARCHAR(255),
    en_description TEXT,
    views VARCHAR(255),
    src_item_id VARCHAR(255),
    src_page_id VARCHAR(255)
);

CREATE INDEX idx_src_item_id ON vertices (src_item_id);
CREATE INDEX idx_src_page_id ON vertices (src_page_id);