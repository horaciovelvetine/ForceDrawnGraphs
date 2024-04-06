DROP TABLE IF EXISTS edges;

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