# JUNG Layouts' Different Approaches
Inside the [JUNG Library](https://github.com/jrtom/jung) there are two different implementations of the Fruchterman-Reingold layout algorithm. In examining these algorithims there's an opportunity to explore some Google (adjacent) code, test the differences, and get into the weeds of optimization choices. There are a few confusing details to iron out here on which implementation is exactly in use, what the differences are, and how the Fruchterman-Reingold layout algorithm actually works. 

- [On Github](https://github.com/jrtom/jung/blob/master/jung-algorithms/src/main/java/edu/uci/ics/jung/layout/algorithms/FRBHVisitorLayoutAlgorithm.java) there appears to be a completely different Barnes-Hut Quadtree optimized version existing under the `FRBHVisitorLayoutAlgorithm.java` class. It is definitley worth the look, with mostly minimal changes to the `calcReupulsion()` 
- [The 2.1.1 docs](https://jrtom.github.io/jung/javadoc/index.html?overview-summary.html) reference a `FRLayout` and `FRLayout2` - this matches with my import and use of the library inside the project so will be the difference examined further.

## Summarizing the Code Differences
  - `FRLayout2` implements handling frozen nodes differently from `FRLayout`, where the first implementation will skip updating pair's that are frozen, `FR2` allows for this one sided relationship by compensating in the calculated change for the unfrozen node. This happens across a few of the methods: `calcPositions() calcAttractions() calcRepulsion()` and from the comments and any reading I could find may improve the overall accuracy and visual appearance of the resultant layout.
  - `FR2` includes some movement constraint logic to limit 'big' jumps in the `calcPositions()` method, given the above optimization it seems these two almost act to balance each others effects across the algorithims methods. 
  - Small changes are made to the way boundaries, and boundary checking are handled. The includsion of a `Rectangle2D innerBounds` at the class variable level provides a class wide access to a means of checking boundaries haven't been exceeded by any member of the layout 
  - The inclusion of a `FRVertexData` class inside of `FRLayout` - `FR2` opts for a simpler implementation and data structure w/o the helpers for normalizing and offsetting which could help speed, but it's unclear if this is a convienence or optimization change. 

## Force Drawn Graphs & The Fruchterman-Reingold Algorithm
Each implementation of a force drawn graph starts by conceptualizing different parts of the solution in really interesting ways, but they all have a similar goal ***minimizing the total energy in a given system***. These calculations take inspiration from real life physical systems, and use them as a means of solving for a practical solution to the layout problem, very cool! Imagining a graph as ["a system of springs connecting steel rings"](docs/references/../../references/Graph%20Drawing%20by%20Force%20Directed%20Placement.pdf) means the system is constantly trying to find a best position for each node. Using [Hooke's Law](https://en.wikipedia.org/wiki/Hooke%27s_law) as a rough interpretation of the forces happening at each spring (`Edge`) gives numerical significance to the distance & forces between them. Allowing for Nodes (`Vertices`) to be frozen in place and repositioned iteratively, and assuming there may never be a "best" solution, the only limitation's imposed here are time and compute power. 

More formal, qualified, and in depth examiniations of this topic are available [here](docs/../references/Graph%20Drawing%20by%20Force%20Directed%20Placement.pdf), but my goal is mainly to summarize at a high level what's happening behind the numbers. 





