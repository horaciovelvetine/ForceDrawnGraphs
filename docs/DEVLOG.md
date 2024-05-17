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

# Thursday May 16, 2024

- [X] Opting not to use records for some of the DTO models recieving data from the API, there is likely a good way to rewrite these 'details' records as Records, but I'll leave that for a refactor, and take not of it here.
- [ ] Storing properties?

Initially the details of the properties are intended to be stored on the WikiDocStmtDetails model - but I'm not sold this is the best way to do this (it potentially adds an additional ent fetch into the cycle, and even 2 async-requests will mean the minimum time for response will be >1s no matter how fast the math is done). While the goal here is to keep the app stateless, either a SQLite cache of the properties, a local memory cache, or even just a small stupid CSV cache stored in the resources folder might be a better way to do this, where as they are fetched their details are stored in a cache, and then the cache is used to get the details of the properties, and fetch the ones we dont already know. 

- [ ] `ingestEntValueSnakAsEdge()` method sets the `srcStmtValue` using a raw `toString()` call, this is likely going to end up with some funky results for the values of the edges for which it used in creating. May need a helper to really narrow down the type of Value being returned here and handle it accordingly.
- [ ] Handling some edge cases as I'm running the edge creation the first time: 

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P1343 :: http://www.wikidata.org/entity/Q67311526 (item)
Statement Rank: NORMAL
Statement ID: Q3454165$6EED15F4-AD5F-4B54-9D95-C55EA9400CE7
Qualifiers: [      http://www.wikidata.org/entity/P2699 :: "https://www.obalkyknih.cz/view_auth?auth_id=xx0025279"
]
References: []
```

In this case, the mainSnak has the target which is an EntValueID, because it uses the Qualifier to store the value of the edge (which is a URL) - this should be potentially handled by retrieving the value of the Qualifier (my concern is this will be a lot of weird external URLs to which I have no affiliation or knowledge/control) or by simply omitting it and using the label exclusively.

The qualifier has an additional property QID which would have to be accomodated for as well.

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P6886 :: http://www.wikidata.org/entity/Q1860 (item)
Statement Rank: NORMAL
Statement ID: Q3454165$93B656E3-6D3F-4DE0-84F4-0BDACFEDE55A
Qualifiers: []
References: []
```

Possibly by solving the first this one will be solved - this has Kevin Bacon writes in English - w/o the label this will be mostly gibberish, and the value of the edge would be the label of the Q1860 entity.

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

Reference value is a date, which may not be worth keeping as a value of the edge, but could be used to weight the edge.

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

Multiple references which have items, TBD on best way to handle this.

```log
2024-05-13 21:41:40
Main Snak: http://www.wikidata.org/entity/P2021 has no value
Statement Rank: NORMAL
Statement ID: Q3454165$20c6e8de-4aef-7752-0281-b8a896866b68
Qualifiers: [      http://www.wikidata.org/entity/P585 :: 2016-00-00 (Prec.: year [-0 .. +0], PCal: Gregorian)
]
References: [  Reference:
      http://www.wikidata.org/entity/P3417 :: "What-is-the-Erdos-Bacon-number-of-Erdos-or-Bacon"
]
```

Not even sure 

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

The whole enchilada. 

Should walk through each of those and sort of see how they are responded to and handled. 