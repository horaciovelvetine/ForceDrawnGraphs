WITH RelevantItems AS (
    SELECT i.item_id, i.en_label, i.en_description
    FROM items i
    INNER JOIN pages p ON p.item_id = i.item_id
    WHERE p.page_id = '1599379' -- EX ID
), RelevantHyperlinks AS (
    SELECT h.from_page_id, h.to_page_id, h.count
    FROM hyperlinks h
    INNER JOIN pages p ON p.page_id = h.from_page_id OR p.page_id = h.to_page_id
    WHERE p.page_id = '1599379' -- EX ID
), RelevantStatements AS (
    SELECT s.source_item_id, s.edge_property_id, s.target_item_id
    FROM statements s
    INNER JOIN RelevantItems ri ON ri.item_id = s.source_item_id OR ri.item_id = s.target_item_id
)
SELECT p.*, ri.*, rh.*, rs.*
FROM pages p
LEFT JOIN RelevantItems ri ON p.item_id = ri.item_id
LEFT JOIN RelevantHyperlinks rh ON p.page_id = rh.from_page_id OR p.page_id = rh.to_page_id
LEFT JOIN RelevantStatements rs ON ri.item_id = rs.source_item_id OR ri.item_id = rs.target_item_id
WHERE p.page_id = '1599379'; -- EX ID
