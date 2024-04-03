DROP TABLE IF EXISTS hyperlinks;

CREATE TABLE hyperlinks (
    id SERIAL PRIMARY KEY,
    from_page_id VARCHAR(255),
    to_page_id VARCHAR(255),
    count VARCHAR(255)
);
