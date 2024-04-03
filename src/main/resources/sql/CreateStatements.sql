DROP TABLE IF EXISTS statements;

CREATE TABLE statements (
    id SERIAL PRIMARY KEY,
    source_item_id VARCHAR(255),
    edge_property_id VARCHAR(255),
    target_item_id VARCHAR(255)
);