CREATE TABLE edges (
  id SERIAL PRIMARY KEY,
  src_vertex_id INT,
  tgt_vertex_id INT,
  weight DOUBLE,
  edge_type VARCHAR(255),
  edge_property_id VARCHAR(255),
)