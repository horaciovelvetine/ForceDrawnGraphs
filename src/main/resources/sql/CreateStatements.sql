DROP TABLE IF EXISTS statements;

CREATE TABLE statements (
    id SERIAL PRIMARY KEY,
    source_item_id VARCHAR(255),
    edge_property_id VARCHAR(255),
    target_item_id VARCHAR(255)
);

CREATE INDEX idx_source_item_id ON statements (source_item_id);
CREATE INDEX idx_edge_property_id ON statements (edge_property_id);
CREATE INDEX idx_target_item_id ON statements (target_item_id);