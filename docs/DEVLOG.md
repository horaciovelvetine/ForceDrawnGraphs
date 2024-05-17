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

- [ ] Opting not to use records for some of the DTO models recieving data from the API, there is likely a good way to rewrite these 'details' records as Records, but I'll leave that for a refactor, and take not of it here.
- [ ] Storing properties?

Initially the details of the properties are intended to be stored on the WikiDocStmtDetails model - but I'm not sold this is the best way to do this (it potentially adds an additional ent fetch into the cycle, and even 2 async-requests will mean the minimum time for response will be >1s no matter how fast the math is done). While the goal here is to keep the app stateless, either a SQLite cache of the properties, a local memory cache, or even just a small stupid CSV cache stored in the resources folder might be a better way to do this, where as they are fetched their details are stored in a cache, and then the cache is used to get the details of the properties, and fetch the ones we dont already know. 
