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

### Module 1 - Creating a local copy of the dataset.

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


#### Optimizing Imports - Testing performance of PreparedStatements

There are more than 141 million statements, and some 60 million additional entries in this (Wikipedia) dataset. Given this volume of data, each call to the DB (to write) will be the most costly part of the process. This write is done using the `PreparedStatement` class [docs](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/PreparedStatement.html), which provides the unique opportunity to dig into the PreparedStatement and see if and how the amount of data included with each commit effects the performance.

Goal: What is the optimal size of the batch to be included in each commit?

Hypothesis: Given the inclusion of the `executeLargeUpdate()` method [docs](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/PreparedStatement.html#executeLargeUpdate()) it is likely there are diminishing returns as the size of the batch is increased, and so the limiting factor will be . Albeit, at some (integer overflow) point the `executeLargeUpdate` method would be required, but I believe this method is intended for running a large update across _all_ rows of a table to adjust something globally (like a timestamp). 

Processes: `BuildLocalSet` contains the majority of the code for this module and is initialized with the `build` command. Tests are run on the same data (item.csv) to compare a variety of batch sizes for each commit. The import process is halted after the `sampleSizeLimit` is reached, and the time taken for the process is recorded. The data was pulled into Excel for analysis and visualization.

Hardware/Software: 
- 2021 16" MacBook Pro
- Apple M1 Max
- 64GB RAM
- Ventura 13.0
- Postgres v2.7.1
- Java 17.0.1
- Spring 3.2.2

- All other data has been cleared out of any local Postgres DBs, each test is being run on a fresh DB hosted and connected to locally. 
- No other applications/processes are running on the machine during the tests (within reason, the OS is still running),and all wireless connections are disabled.
- The internal drive is approximately 25% full and is 2.0TB in size. 

Results: Data.