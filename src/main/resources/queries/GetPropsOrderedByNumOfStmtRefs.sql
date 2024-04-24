SELECT
    p.id,
    p.property_id, 
    p.en_label, 
    p.en_description, 
    COUNT(s.id) AS reference_count
FROM 
    properties p
LEFT JOIN 
    statements s ON p.property_id = s.edge_property_id
GROUP BY 
    p.id, p.property_id, p.en_label, p.en_description
ORDER BY 
    reference_count DESC;