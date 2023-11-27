-- BEGIN: Drop tables if exist
DROP TABLE IF EXISTS wikiset;
-- END: Drop tables if exist

-- BEGIN: Create wikiset table
CREATE TABLE wikiset (
  id INT,
  notes VARCHAR(255)
);
-- END: Create wikiset table

-- BEGIN: Insert data into wikiset table
INSERT INTO wikiset (id, notes)
VALUES (1, 'LocalConnectionPresent');
-- END: Insert data into wikiset table
