DROP TABLE IF EXISTS wikiset;
CREATE TABLE wikiset (
  id SERIAL PRIMARY KEY,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total_item_alias_records INTEGER,
  total_item_records INTEGER,
  total_link_annotated_text_records INTEGER,
  total_page_records INTEGER,
  total_property_alias_records INTEGER,
  total_property_records INTEGER,
  total_statement_records INTEGER
);
