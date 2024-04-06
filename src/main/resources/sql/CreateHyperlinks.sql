DROP TABLE IF EXISTS hyperlinks;

CREATE TABLE hyperlinks (
    id SERIAL PRIMARY KEY,
    from_page_id VARCHAR(255),
    to_page_id VARCHAR(255),
    count VARCHAR(255)
);
CREATE INDEX idx_from_page_id ON hyperlinks (from_page_id);
CREATE INDEX idx_to_page_id ON hyperlinks (to_page_id);