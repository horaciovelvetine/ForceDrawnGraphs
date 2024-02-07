-- Migration for creating 'items' table
CREATE TABLE items (
    id INT PRIMARY KEY,
    item_id INT,
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref INT
);

-- Migration for creating 'pages' table
CREATE TABLE pages (
    id INT PRIMARY KEY,
    page_id INT,
    item_id INT,
    title VARCHAR(255),
    views INT,
    line_ref INT
);

-- Migration for creating 'hyperlinks' table
CREATE TABLE hyperlinks (
    id INT PRIMARY KEY,
    from_page_id INT,
    to_page_id INT,
    count INT,
    line_ref INT
);

-- Migration for creating 'properties' table
CREATE TABLE properties (
    id INT PRIMARY KEY,
    property_id INT,
    en_label VARCHAR(255),
    en_description TEXT,
    line_ref INT
);

-- Migration for creating 'statements' table
CREATE TABLE statements (
    id INT PRIMARY KEY,
    source_item_id INT,
    edge_property_id INT,
    target_item_id INT,
    line_ref INT
);

