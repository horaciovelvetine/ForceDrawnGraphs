DROP TABLE IF EXISTS valid_connection;
CREATE TABLE valid_connection (
  id INT,
  is_present BOOLEAN,
  valid_message VARCHAR(255)
);
INSERT INTO valid_connection (id, is_present, valid_message)
VALUES (1, true, 'Valid connection to the DB found.');

