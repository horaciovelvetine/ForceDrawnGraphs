# Firday May 10, 2024

- After exploring the Wikidata toolkit, it appears there is a really low bar to entry for access to the DATA that may be a great starting point, and just fast enough to back a performant web app. Major changes are reflexting a change of direction in the data source, and a "v2" to sort out the older models built out of the previous data source.
- [ ] Does the wikimedia foundation have a host/platform which might provide a consistent, faast, and reliable way to both host the backend (which should be essentially stateless) and provide near access to the data. Initial testing suggests that at any given second the query time is between 300-800ms. - but the only way to improve this (given this approach) is outside of this domain.
- [ ] Given the limited number of properties, it may make sense to keep these in local memory (not delegate this to wikimedia), or delegate it, but fetch this on startup (once per period of time, on command, etc) then keep a copy in memory to remove this query and speed up response time.

# Sunday May 12, 2024
- [ ] The StatementGroupImpl class has a getBestStatements() method which mentions the StatementRank.ENUM --> given this exists might be something to leveraging in weighting the edges of the graph.
- [ ] The entry is a search by title which is extremely ridgid and will likely need to be replaced with something flexible in case the search by title fails to find a suitable starting place

# Monday May 13, 2024
- [ ] https://www.mediawiki.org/wiki/API:Links -> for getting some links
- [ ] Some of the statements (when initially fetched) come with an attribute of time (which is a date) - where older statements could (slightly) be weighted less than newer ones.
- [ ] https://vscode.dev/github/horaciovelvetine/ForceDrawnGraphs/blob/wikidata-toolkit-examples-starttk.wikibaseapi/WikibaseDataFetcher.class#L63 --> theres a set 50 limit on the number of statements fetched, which may be a good starting point for the number of edges to fetch at a time.

# Tuesday May 14, 2024

- It would be tremendously cool to have the final web app be tremendously bulletproof, I want to make error handling and testing a priority. (Writing my famous last words here) 

# Thursday May 16 - Sundayy May 19, 2024

- [X] Opting not to use records for some of the DTO models recieving data from the API, there is likely a good way to rewrite these 'details' records as Records, but I'll leave that for a refactor, and take not of it here.
- [ ] Initially the details of the properties are intended to be stored on the `WikiDocStmtDetails` model - but I'm not sold this is the best way to do this (it potentially adds an additional ent fetch into the cycle, and even 2 async-requests will mean the minimum time for response will be >1s no matter how fast the math is done). While the goal here is to keep the app stateless, either a SQLite cache of the properties, a local memory cache, or even just a small stupid CSV cache stored in the resources folder might be a better way to do this, where as they are fetched their details are stored in a cache, and then the cache is used to get the details of the properties, and fetch the ones we dont already know. 

- [ ] `ingestEntValueSnakAsEdge()` method sets the `srcStmtValue` using a raw `toString()` call, this is likely going to end up with some funky results for the values of the edges for which it used in creating. May need a helper to really narrow down the type of Value being returned here and handle it accordingly. **UPDATE: Commonly a double quoted value is returned e.g. ""foo"" - often a string and can be handled witha  simple `replace("\"", "")` call.**

## Real Statement Cases to Analyze:

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P1343 :: http://www.wikidata.org/entity/Q67311526 (item)
Statement Rank: NORMAL
Statement ID: Q3454165$6EED15F4-AD5F-4B54-9D95-C55EA9400CE7
Qualifiers: [      http://www.wikidata.org/entity/P2699 :: "https://www.obalkyknih.cz/view_auth?auth_id=xx0025279"
]
References: []
```

- DEFINES: (P1343) described by source (Q67311526) obalkyknih.cz
- NOTES: Qualifier contains the (P2699) URL of the source - this could be easily ignored, but should be left open for additional statements with this pattern to determine a trend for properties with qualifiers. In this case the original QID is the target, which will link to the parent site, and render the URL redundant information. The property value of the qualifier will likely be the last line of defense determining wether the qualifier matters.
- BREAKDOWN: 
    - Check for refs
    - Check for qualifiers
    - Check for URL in qualifiers
    - Check for QID in qualifiers
    - Debug stopper at this pattern end to assess, and determing a method for going forward.
- FINAL NOTE: Assuming the URL is ignroed this statement could be handled the same as the below statement.
    - Check no refs or qualifiers: source of propTypeQID/Label/Value are from P6886, Q1860 is the tgtEntID stored on the edge


*The qualifier has an additional property QID which would have to be accomodated for as well.*

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P6886 :: http://www.wikidata.org/entity/Q1860 (item)
Statement Rank: NORMAL
Statement ID: Q3454165$93B656E3-6D3F-4DE0-84F4-0BDACFEDE55A
Qualifiers: []
References: []
```

- DEFINES: (P6886) writing language (Q1860) English
- NOTES: Needs no additional context data
- BREAKDOWN:
      - Check no refs or qualifiers: source of propTypeQID/Label/Value are from P6886, Q1860 is the tgtEntID stored on the edge

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P2031 :: 1978-00-00 (Prec.: year [-0 .. +0], PCal: Gregorian)
Statement Rank: NORMAL
Statement ID: Q3454165$793fa6b0-4c5d-fa86-fc78-1de1915fd4ef
Qualifiers: []
References: [  Reference:
      http://www.wikidata.org/entity/P143 :: http://www.wikidata.org/entity/Q206855 (item)
]
```

- DEFINES: `P2031` work period (start) date for the value *1978*, imported from (P143) the Russian Wikipedia (Q206855)
- NOTES: In this case the ref is to the Russian Wiki, which is unimportant as the source in this case could have been anywhere. However the value of the `mainSnak` is a date 1978 which has an associated QID (Q1860) `ItemDocument` - this would provide a direct "timeline" weighting to the graph which would is important.
- BREAKDOWN:
    - Check for mainSnak value type (after has been cast to `ValueSnak`), if it is a `TimeValue` cast to a helper to determine the QID of the date, and store that as the tgtEntID on the edge.
    - Similarly the `stmtDetails` would be as the QID/Label/Value for (P2031)

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P7859 :: "lccn-n88034930"
Statement Rank: NORMAL
Statement ID: Q3454165$5F7CCC15-596C-41AE-9B5F-9D3C58473AA4
Qualifiers: []
References: [  Reference:
      http://www.wikidata.org/entity/P214 :: "39570812"
,   Reference:
      http://www.wikidata.org/entity/P887 :: http://www.wikidata.org/entity/Q1266546 (item)
      http://www.wikidata.org/entity/P248 :: http://www.wikidata.org/entity/Q14005 (item)
      http://www.wikidata.org/entity/P434 :: "cc0dbdfc-9b2c-4e31-8448-808412388406"
      http://www.wikidata.org/entity/P813 :: 2021-10-04 (Prec.: day [-0 .. +0], PCal: Gregorian)
]
```

- DEFINES: `P7859` WorldCat Identities ID (superseded) `lccn-n88034930` - first reference is `P214` a VIAF ID (virtual international authority ID) "39570812" - second reference is `P887` based on heuristic `Q1266546` record linkage `P248` (essentially this is to the word to) stated in `Q14005` MusicBrainz (an online music metadata DB), where the `P434` MusicBrainz artist ID is "cc0dbdfc-9b2c-4e31-8448-808412388406" and the `P813` date of the reference was last pulled is 2021-10-04.
- NOTES: This one is a bit of a chore
    - The VIAF is a authority file host/aggregator meant to help better Identify: Names, Locations, Works, and Expressions across different languages and Data sources. 
    - MusicBrainz is a random open music metadata DB that is mostly irrelevant.
    - Last is the WorldCat Identities ID which is a unique identifier for a person in the WorldCat database. (the P7859) this is the only one that *could* have had some links - but I think if I am omitting random DB linkages (i.e. MusicBrainz) consistency dictates that I should omit this as well (similar to the above Russian wiki ref which is also essentially ignored for the more pertinent info - in this case there is none).
    - *WHAT IF* there was a way to look at the `.value()` for the SNAKs and determine if these external IDs have any sort of elicit typing which could be used to reject processing these statements as edges.
- BREAKDOWN:
  - Ignore this son of a gun.       


```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P166 :: http://www.wikidata.org/entity/Q251542 (item)
Statement Rank: NORMAL
Statement ID: Q3454165$8c7f0cac-420e-ea78-c6b6-48f0bd29ce45
Qualifiers: [      http://www.wikidata.org/entity/P585 :: 2009-00-00 (Prec.: year [-0 .. +0], PCal: Gregorian)
,       http://www.wikidata.org/entity/P1686 :: http://www.wikidata.org/entity/Q935173 (item)
]
References: [  Reference:
      http://www.wikidata.org/entity/P143 :: http://www.wikidata.org/entity/Q206855 (item)
]
```
- DEFINES: `P166` award received `Q251542` the Golden Globe Award for Best Actor - Miniseries or Television Film, the first qualifier is `P585` point in time `2009` and the second qualifier is `P1686` for the work `Q935173` Taking Chance (movie). The reference is (again for some reason) the Russsian Wiki where this info was sourced. 
- NOTES: This is a good example of a Qualifier Groups - where the important info here is at the end of the chain `Q935173` the movie Taking Chance. This is a clear example of an edge between KB and a the movie. BUT the qualifier `P585` is a date which (similar to the above) may be an edge worth creating on the graph targeted at the year 2009 (Q1996 - oh the irony). The reference can be ignored (although it would be fun to see how much data is just sourced from Russian Wiki for whatever reason). 
- BREAKDOWN:
  - mainSnak has an edge connecting KB and a specific Golden Globe
  - the qualifiers contain two edges one to the year 2009 and one to the movie Taking Chance
  - the reference can be ignored


**The good news is we got a second Bacon out of this, Hungarian Mathematician Paul Erdos has a similar numbering system, and could be used as a second origin to build this off of.**

- [ ] The java ver target was changed from 17 to 21 to provide access to a more reader freindly convention for switch statements. These statements require ints, strings, or Enums - but theres refactor to be had here 
- [X] Since on a statement they carry the QID through the getSubject() method, in theory the procDoc can ignore passing this QID value all the way down the chain... `statement.getSubject().getId()`
- [ ] The intended design pattern - after some reasearch and reading - is to use a Visitor pattern to pull the data from WikiData ents, this should help with the immense type casting mess that is currently the `WikiDocProcessor` class.

# Thursday May 23, 2024

 Having worked through the process and rebuilding the Statement processing for the 3rd time - this new approach creates a service class to have the logic live in one container for each statment as it gets processed. This abstracts the (even though it is now using a visitor pattern) still huge mess of logic needed to begin this process in the `EntDocProc` class. In that process a few, similar to the above, specific statements require specific handling, and below I'm going to list out a few of the specific cases encountered, and how they were handled in the new service class.

- [ ] The datatype "external-id" existing on the mainSnak is always an immediate disqualifier.
- [ ] The `P1343` property is a URL to the source of the statement - this is always (in theory) external, as internal wikidata will use a property defined to be an internal wikidata item.