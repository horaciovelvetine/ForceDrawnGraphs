DROP TABLE IF EXISTS pages;

CREATE TABLE pages (
    id SERIAL PRIMARY KEY,
    page_id VARCHAR(255),
    item_id VARCHAR(255),
    title VARCHAR(255),
    views VARCHAR(255)
);

CREATE INDEX idx_page_id ON pages (page_id);
CREATE INDEX idx_page_item_id ON pages (item_id);