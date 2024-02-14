-- Migration for creating 'items' table
CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    item_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref int
);

-- Migration for creating 'pages' table
CREATE TABLE pages (
    id SERIAL PRIMARY KEY,
    page_id VARCHAR(255),
    item_id VARCHAR(255),
    title VARCHAR(255),
    views VARCHAR(255),
    line_ref INT
);

-- Migration for creating 'hyperlinks' table
CREATE TABLE hyperlinks (
    id SERIAL PRIMARY KEY,
    from_page_id VARCHAR(255),
    to_page_id VARCHAR(255),
    count VARCHAR(255),
    line_ref INT
);

-- Migration for creating 'properties' table
CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    property_id VARCHAR(255),
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref INT
);

-- Migration for creating 'statements' table
CREATE TABLE statements (
    id SERIAL PRIMARY KEY,
    source_item_id VARCHAR(255),
    edge_property_id VARCHAR(255),
    target_item_id VARCHAR(255),
    line_ref INT
);

-- Migration for creating 'local_set_progress_trackers' table
-- For tracking the progress of building the local PG set
CREATE TABLE local_set_info (
    id SERIAL PRIMARY KEY,
    items_imported INT,
    total_items INT,
    pages_imported INT,
    total_pages INT,
    hyperlinks_imported INT,
    total_hyperlinks INT,
    properties_imported INT,
    total_properties INT,
    statements_imported INT,
    total_statements INT
);


-- Inserts a default row into the 'local_set_progress_trackers' table
-- This row will be used to track the progress of building the local PG set
INSERT INTO local_set_info (
    id,
    items_imported,
    total_items,
    pages_imported,
    total_pages,
    hyperlinks_imported,
    total_hyperlinks,
    properties_imported,
    total_properties,
    statements_imported,
    total_statements
) VALUES (
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0
);
