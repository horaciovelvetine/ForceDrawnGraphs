<div align=center>
  <h1> Force Drawn Graphs</h1>
  <h4> What does wikipedia look like in 3D space? </h4>
<h2> Summary </h2>
<p>The goal of this project ultimatley is to visualize Wikipedia in 3D (& 2D) space based on how related each entity is to one another. Given the enormity of this sort of problem - the primary goal of this  README will be to highlight and follow the iterative problem solving process to tackle a huge problem, highlighting lessons learned along the way as a novice working with big data sets.<p>
  <a href="https://www.wikidata.org/wiki/Wikidata:Main_Page">
    <img src="docs/../images/Wikidata-logo-en.svg" width="175">
  </a><a href="https://www.kaggle.com/datasets/kenshoresearch/kensho-derived-wikimedia-data">
    <img src="docs/../images/Kensho-data-en.svg" width="175">
  </a>
<h2>The Data</h2>
<br>
</div>

Wikipedia is an enormously cool resource which has an even cooler non-profit cousin called [Wikimedia](https://wikimediafoundation.org/). The Wikimedia Foundation is a non-profit organization that hosts Wikipedia and other free knowledge projects, one of these is [Wikidata](https://www.wikidata.org/wiki/Wikidata:Main_Page). This project is focused on providing wide access to the data behind wikipedia, and is essential in providing data to crunch for free.  *(It should be noted that featured as a background image is a Graph, ultimaltey the solution I took an enormous detour to arrive back at)*

Initially this project used a dataset from Kaggle, which was a smaller subset of a larger Wikidata dump from english wikipedia in 2019 [Kensho Derived Wikimedia Dataset](https://www.kaggle.com/datasets/kenshoresearch/kensho-derived-wikimedia-data/). This dataset was used to design the initial importing and some initial processing, but after some digging has been found to have some unreliable data. Later on there will be outlines with new data & table structure, but for now the below information will reference process' and code that was used with the Kaggle dataset. The dataset is approximatley 30GB in size, and contains both CSV files and a single JSONL file with all of the data and 5 primary tables that are important for the intended purpose: Statements, Items, Pages, Hyperlinks, and Properties.

<h2> Kensho Dataset Table Diagram </h2>

![Kensho Dataset Table Diagram](docs/../images/KenshoDatasetChart_v2.0.drawio.svg)

<h4>Understanding the Dataset to Import It</h4>

The first step is bringing the data (downloaded from the link above) into the local Postgres Database so that it can be processed into the needed form. In order to view Wikipedia in 3D space there has to be a base definition for "space" - and in this case that will be the calculated relationship between entities (represented by either Pages, Items or a combo of the two). During the import/insert process, the data from the `link_annotated_text.jsonl` file was changed before being inserted into the `Hyperlinks` table. This process took the various links, and incremented the `count` column in the table to represent the number of times a link was found referenced connecting two pages. 

Initially, the `Item` & `Statements` relationship(s) were a bit tough to understand for me, but this  is an extremely flexible and smart way of storing the complex data found in Wikipedia. An `Item` can be used in so many ways, but a great way to wrap your mind around it is to think of Albert Einstien. In order to represent "things" about him there needs to be a way of categorizing the "thing" you want to record about him. For example, you could say "Albert Einstien *is a* physicist". In this case, "Albert Einstien" is an `Item`, "*is a*" is a `Statement` (in this case pointing to a `Property` with the label "profession"), and "physicist" is also an `Item`. Once this makes some sense it's easy to see how this sis used to represent a lot of complex data in a very high level and simple way. 

A seperate process was used to asses the number_of_refs for `Properties` wherein the number of entities refrencing a `Property` were counted and stored back in the `Properties` table with the associated record. This step is meant to mimick the `count` process for `Hyperlinks` and provide a quantifiable attribute to base the relationship between entities on.

<h4>Gotta Start Somewhere</h4>

Being a novice and finding resources for solving the big idea in your head often involves the enormous frustration of not knowing where to start. I started this project with pretty minimal knowledge regarding cartesian coordinates, working with big datasets, and not a single clue that Force Directed Graphs were a thing. 

Conceptually I knew that `(x,y,x)` coordinates could be used to represent a "Wikipedia Page" in 3D space, and that the relationship between pages could be represented by the distance between them. In practice a pretty simple concept, so thats where I started, a pretty basic idea, averages. This led to building a tool and doing some reasearch for existing solutions for building graphs, and you can find more detail on that in this repository: [Finding Centroids](https://github.com/horaciovelvetine/finding-centroids)

<a href="https://github.com/horaciovelvetine/finding-centroids"><img src="docs/../images/wmeans_3_vertices_2d_with_strength_radius.png"></a>

<h4>Force Directed Graphs</h4>

<div align="center">
<h3>Steps for Running Locally</h3>
</div>

1. Clone the repository to your local machine
2. Configure your machine for running the main Class `ForceDirectedGraphsApplication`. Since this project was build inside VSCode the `.vscode` folder is included in the repository, and the `launch.json` file is configured to run the main class (in VSCode this will include downloading the Java Extension Pack and configuring the `launch.json` file to run the main class).
3. Create a new Postgres Database and configure the `application.properties` file to point to the new database, below there is the configuration I used. In addition PGAdmin4 was used in managing, and viewing data and creating the database in itself (providing a GUI for the database).
4. Run the main class to begin the Spring Shell application - `help` will provide a list of commands that can be run.


<h4>Sample VSCode Run Config:</h4>

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug (Launch)-ForceDrawnGraphsApplication<ForceDrawnGraphs>",
            "request": "launch",
            "mainClass": "edu.ForceDrawnGraphs.ForceDrawnGraphsApplication",
            "projectName": "ForceDrawnGraphs",
            "cwd": "${workspaceFolder}",
            "vmArgs": "-Xmx9g"
        }
    ]
}
```
<h4>Sample `application.properties` File:</h4>

```properties
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.hibernate.show-sql=true
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/en_Wikipedia_page_text_12_01_2019
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.continue-on-error=true
```

<div align="center">
  <h3> References, Relevant Links, and Further Reading</h3>
</div>

- [Force-Directed Drawing Algorithms by Stephen G. Kobourov (from Brown University CS curriculum)](https://cs.brown.edu/people/rtamassi/gdhandbook/chapters/force-directed.pdf)
- [Handbook of Graph Drawing and Visualization by Roberto Tamassia (Readings Overview)](https://cs.brown.edu/people/rtamassi/gdhandbook/)
- [Spring Embedders and Force Directed Graph Drawing Algorithms by Stephen G. Kobourov (from the University of Arizona)](https://arxiv.org/pdf/1201.3011.pdf;)
- [JUNG2 2.0 API Documentation](https://jung.sourceforge.net/doc/api/index.html)
- [FADE: Graph Drawing, Clustering, and Visual Abstraction by Aaron Quigley and Peter Eades (from the University of Newcastle, Callaghan NSW)](https://arxiv.org/pdf/1201.3011.pdf;)