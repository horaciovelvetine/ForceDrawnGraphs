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
