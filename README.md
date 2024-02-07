# ForceDrawnGraphs

Original Dataset [here](https://www.kaggle.com/datasets/kenshoresearch/kensho-derived-wikimedia-data)

Goal: analyze the data to create a graph set representation reflection the relative relationship between wikipedia pages.

## Configuration

The dataset linked above is >20GB and requires some time to download, however a local copy is needed in `src/main/resources/data/`. Additional configuration will be required for your preferred local DB, this implements a local Postgres DB to track the graph data.

## Running

This application uses Spring-Shell to provide a CLI interface to run the various commands `help` will list all of the available commands and a summary of what they do

## Resources

- Spring-Shell: [site](https://docs.spring.io/spring-shell/docs/current/reference/htmlsingle/)
- Overview of Spring-Shell: [site](https://reflectoring.io/spring-shell/)
- JUNG: Java Universal Network/Graph Framework  [site](https://jung.sourceforge.net/)

### Module 1 - Importing the Data

First step is to import the variety of files the dataset provides, including mostly .csv and a .jsonl file. Each file gets a corresponding table. For now aliases will be excluded. The PK ID for each model will be internal to the local PG DB, while the original ID-ing structure will remain stored as ints, this will allow any FK contraints to be mitigated on import.

![Wikiset Breakdown Diagram](/docs/Wikiset%20Breakdown%20Diagram%20v0.2.svg)

- [ ] items 
  - id (int)
  - item_id (int)
  - en_label (string)
  - en_description (string)

- [ ] pages 
  - id (int)
  - page_id (int)
  - item_id (int)
  - title (string)
  - views (int)

- [ ] hyperlinks
  - id (int)
  - from_page_id (int)
  - to_page_id (int)
  - count (int)

- [ ] properties
  - id (int)
  - property_id (int)
  - en_label (string)
  - en_description (string)

- [ ] statements
  - id (int)
  - source_item_id (int)
  - edge_property_id (int)
  - target_item_id (int)