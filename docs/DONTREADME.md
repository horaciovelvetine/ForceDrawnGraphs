## Force Directed Graphs

Part of the reason the original Kaggle dataset was used was to prevent having to learn the large and intimidating Wikidata API. Given the new data source, there are some advantages to using the Wikidata API, the data is much more reliable, and this project doesn't have to even neccasarily need a databse to run. 

Ths Wikidata API has a variety of tools to give access to various parts of the Media Wiki database, and even has the <a href=https://github.com/Wikidata/Wikidata-Toolkit>Wikidata-Toolkit library</a> which provides some Java tooling for working with the API. The <a href=https://github.com/Wikidata/Wikidata-Toolkit-Examples>Wikidata-Toolkit-Examples</a> is the place to start, and possibly the simplest request example is just finding Entities by their ID/Title/Query. After submitting a <a href=https://github.com/Wikidata/Wikidata-Toolkit-Examples/pull/6>Pull Request</a> to the Wikidata-Toolkit-Examples repository to spruce up the docs, it's time to recreate the above process with all new data.

Fetching data from the API (using the example title "Kevin Bacon") returns an EntityDocument object which looks a bit like this: 
```txt
==ItemDocument http://www.wikidata.org/entity/Q3454165 (r2121385886) ==
* Labels: "Kevin Bacon" (af); "Kevin Bacon" (an); "كيفين بيكن" (ar); (cont...)
* Descriptions: "Amerikaanse akteur" (af); "ممثل أمريكي" (ar); "actor estauxunidense" (ast); (cont...)
===Statements===
[ID Q3454165$895A58DB-9E28-431B-B98B-7E078866AB4F] http://www.wikidata.org/entity/Q3454165 (item): http://www.wikidata.org/entity/P269 :: "067287832"
      http://www.wikidata.org/entity/P1810 :: "Bacon, Kevin (1958-....)"
[ID Q3454165$CD87A968-69C0-4DE2-BA30-206BA61120A0] http://www.wikidata.org/entity/Q3454165 (item): http://www.wikidata.org/entity/P268 :: "139817766"
  Reference:
      http://www.wikidata.org/entity/P248 :: http://www.wikidata.org/entity/Q54919 (item)
      http://www.wikidata.org/entity/P214 :: "39570812"
      http://www.wikidata.org/entity/P813 :: 2018-10-07 (Prec.: day [-0 .. +0], PCal: Gregorian)
[ID Q3454165$C81A3986-19EC-4E9D-9C69-7229A4D13432] http://www.wikidata.org/entity/Q3454165 (item): http://www.wikidata.org/entity/P7293 :: "9810630778405606"
[ID Q3454165$D759CE77-61CD-493D-929C-C7C620175FA8] http://www.wikidata.org/entity/Q3454165 (item): http://www.wikidata.org/entity/P2435 :: "4660"
  Reference:
      http://www.wikidata.org/entity/P143 :: http://www.wikidata.org/entity/Q53464 (item)
(cont...)
===End of statements===
* Site links: afwiki/Kevin Bacon; anwiki/Kevin Bacon; arwiki/كيفين بيكن; arzwiki/كيفين بيكن; astwiki/Kevin Bacon; (cont...)
```
The most important part of this info is: label, description, and QID which are all used to create a vertex. The Statements can then be parsed to find any possible edge relationships.

```txt
ID: Q3454165$e14c32ff-4e8b-d0b8-8af2-2c8c73aee3a7
Rank: NORMAL
Main Snak: http://www.wikidata.org/entity/P166 :: http://www.wikidata.org/entity/Q1275727 (item)
Qualifiers: [
  http://www.wikidata.org/entity/P585 :: 2013-00-00 (Prec.: year [-0 .. +0], PCal: Gregorian),
  http://www.wikidata.org/entity/P1686 :: http://www.wikidata.org/entity/Q1189631 (item)
]
References: [  
  Reference:
    http://www.wikidata.org/entity/P143 :: http://www.wikidata.org/entity/Q206855 (item)
]
```

This is a sample of the kind of statement found with an EntityDocument with all of the important info removed to better display the structure, and the type of statment data important for creating an edge. 

- **ID**: QID is the MediaWiki unique identifier for the entity (where Q is the namespace for entities, and P is the namespace for properties)
- **Rank**: The rank of the statement which is either: NORMAL, PREFERRED, or DEPRECATED, but mostly seems to help highlight certain statements for UI/Display purposes.
- **Main Snak**: The main snak is the primary statement, and in this case is a property (P166: "Award Received") with a value (Q1275727: Saturn Award for best Actor on Television) which is another entity.
- **Qualifiers**: Qualifiers are additional contextual information about the statement, in this case the day the award was received, and the film for which the award was received (Q1189631: The Following).
- **References**: References are sources for the information in the statement, in this case the source is the "award received" statement on the entity for the Saturn Awards (which was P143: imported from Wikimedia project Q206855: Russian Wikipedia).