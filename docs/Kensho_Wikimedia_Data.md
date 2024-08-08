<div align=center>
<h3> Kensho Dataset Table Diagram </h3>
<img src="docs/images/KenshoDatasetChart_v2.0.drawio.svg"/>
</div>

<h3>Understanding the Dataset and Importing It</h3>

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

The first step is bringing the data (downloaded from the link above) into the local Postgres Database so that it can be processed into the needed form. In order to view Wikipedia in 3D space there has to be a base definition for "space" - and in this case that will be the interpreted relationship between entities (represented by either Pages, Items or a combo of the two). During the import/insert process, the data from the `link_annotated_text.jsonl` file was changed before being inserted into the `Hyperlinks` table. This process took the various links, and incremented the `count` column in the table to represent the number of times a link was found referenced connecting two pages. 

Initially, the `Item` & `Statements` relationship(s) were a bit tough to understand for me, but this  is an extremely flexible and smart way of storing the complex data found in Wikipedia. An `Item` can be used in so many ways, but a great way to wrap your mind around it is to think of Albert Einstien. In order to represent "things" about him there needs to be a way of categorizing the "thing" you want to record about him. For example, you could say "Albert Einstien *is a* physicist". In this case, "Albert Einstien" is an `Item`, "*is a*" is a `Statement` (in this case pointing to a `Property` with the label "profession"), and "physicist" is also an `Item`. Once this makes some sense it's easy to see how this is used to represent a lot of complex data in a very high level and simple way. 

A seperate process was used to asses the number_of_refs for `Properties` wherein the number of entities refrencing a `Property` were counted and stored back in the `Properties` table with the associated record. This step is meant to mimick the `count` process for `Hyperlinks` and provide a quantifiable attribute to base the relationship between entities on.

<h4>The Numbers & Underlying Java</h4>

The original Kensho dataset is about 25GB of data, and being naive to any approaches to handling this amount of data, the initial import strategy was to traverse, parse, and insert each and every (line for CSV, JSON object for JSONL) record into the database. While the only data I have from this process is anecdotal, I was esimating this insert process to take longer than 2 weeks to complete. 

With some searching you will find that there are a few ways better to approach this, a common method is to use the `COPY` command in postgres to directly insert the data from the files into the database, but since the above stated goal was to interpret the data on the way in, and discard some (the text for each page...), another method was used. Given the simplicity of the data, and the fact that the data was already in a format that could be easily parsed, the `BufferedReader` & `PreparedStatement` classes allow processing each line for large batch inserting into the dataset.

<h4>Module 1: Understanding & Testing Prepared Statements</h4> 

The `PreparedStatement` class is a subclass of `Statement` that allows for precompilation of SQL, which can then be comitted in large batches. In this case there are some 250 million rows being written to the database, which provides an excellent chance to test the performance and better understand how batch size effects the performance of the insert process. The change from individual inserts to batch inserts **is the single largest optimization, reducing time to write to under an hour**.

<div align=center>
  <p>*details about hardware specifications can be found at the end of this README, the PG instance is hosted locally, and no other process were running during each test*</p>
</div>

The below data outlines two sets of tests run inserting the dataset utilizing variable batch sizes, timing the performance, and reporting it in a log file. Initial batches are run with sizes of: 100, 1000. 2500, 500, 10000, 25000, 50000, and 100000 objects per commit, but the subsequent tests and charts will picture runs with batch sizes of: 100000, 250000, 500000, 1000000, and 10000000 objects per commit (The inital runs were recorded and iterated on, suffering at large from the same costly time per `INSERT` statement).

<div align=center>
  <img src="docs/images/PreparedStatementsChart_v1.1.1.png" width=1600>
</div>
<p>This data was limited to inserting 10 million records from the `item.csv` set, but outlines a really interesting stepping trend emerging as the batch size reaches 250,000 objects. With each batch being run at least 3 times, then averages calculated (and displyed above) 10m & 100k batch sizes emerged as plausible, later on machine limitations would choose the 100k batch size for the final import process. A better understanding of the `PreparedStatement` class may have an explanation for the stepping trend but that lies outside the scope of this project, and will have to remain a mystery for now.</p>
<hr>

<h4>Module 2: Leveraging Multiple Threads</h4>

The last formal optimization in the import step was leveraging multiple threads to run the insert process asyhronously. This was done by creating a `ThreadPoolCache` and then running the import process for each file at the same time. With the batch size at 100k, this allowed paralell processing of the `Statements` and `Hyperlinks` tables. This final optimization has a **tremendous** impact, cutting the time to import in half. Mechanically this is equivalent to writing 250 million rows (all of english Wikipedia circa 2019) representing the relationships between all pages and their listed statements from an average time of *30* minutes to *15 under minutes*.
<div align=center>
  <img src="docs/images/LeveraginMultipleThreadsAvgs.png" width=1600>
  <img src="docs/images/LeveraginMultipleThreadsAvgs.png" width=1600>
</div>
<hr>