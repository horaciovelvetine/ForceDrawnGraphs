DROP TABLE IF EXISTS statements;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS hyperlinks;
DROP TABLE IF EXISTS pages;
DROP TABLE IF EXISTS items;

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    item_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref int
);

CREATE TABLE pages (
    id SERIAL PRIMARY KEY,
    page_id VARCHAR(255),
    item_id VARCHAR(255),
    title VARCHAR(255),
    views VARCHAR(255),
    line_ref INT
);

CREATE TABLE hyperlinks (
    id SERIAL PRIMARY KEY,
    from_page_id VARCHAR(255),
    to_page_id VARCHAR(255),
    count VARCHAR(255),
    line_ref INT
);

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    property_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref INT
);

CREATE TABLE statements (
    id SERIAL PRIMARY KEY,
    source_item_id VARCHAR(255),
    edge_property_id VARCHAR(255),
    target_item_id VARCHAR(255),
    line_ref INT
);

-- BEGINS GRAPHSET TABLES --

DROP TABLE IF EXISTS edges;

CREATE TABLE edges (
  id SERIAL PRIMARY KEY,
  src_vertex_id INT,
  tgt_vertex_id INT,
  weight FLOAT,
  edge_type VARCHAR(255),
  edge_property_id VARCHAR(255)
);

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
    src_page_id VARCHAR(255)
);