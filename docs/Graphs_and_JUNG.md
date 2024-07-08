# Graphs, the J.U.N.G. (Java Universal Network Graph) Library, and YOU

- [ ] Insert AI generated Uncle Sam-esque recruitment "YOU" poster image

This document and codebase are only a summary of the research done in the course of this project, if you're interested in understanding there is a [reference handbook](docs/../references/graphs_handbook_full/0_contents.pdf) which does a tremendous job summarizing about every piece of Graph Theory out there. [Chapter 12: Force Directed Graphs](dev/../references/graphs_handbook_full/12_force-directed.pdf) is the chapter relevant to the rest of this doc, and was written by [Stephen G. Kobourov](https://scholar.google.com/citations?user=P21gHIkAAAAJ&hl=en) a widely cited and recognized expert in the field.

## Force Drawn Graphs & The Fruchterman-Reingold Algorithm
Force Drawn Graph layout algorithims take inspiration from real life physical systems, and use them as a means of solving for a practical solution to the generating of a layout problem. Imagining a graph as ["a system of springs connecting steel rings"](docs/../references/1201.3011v1.pdf) conceptualizes a system constantly trying to find a best position for each ring (node). Then using physical law's and constants like [Hooke's Law](https://en.wikipedia.org/wiki/Hooke%27s_law) or [Coulomb's Law](https://en.wikipedia.org/wiki/Coulomb%27s_law) to give numerical significane to the forces happening at each spring (`Edge`), this suddenly becomes a very (big) recursive math problem.

The above summary does a lot to smooth over huge intellectual accomplishments in the field of Graph Theory. [W.T. Tutte's](https://en.wikipedia.org/wiki/W._T._Tutte) early work on Graph layout's very much mirror the initial ideation of a layout which started this app. [Finding Centroids](https://github.com/horaciovelvetine/finding-centroids) is a visualization of the exact concept Tutte used, and [the Weighted Average Layout Readme](docs/../WEIGHTED_AVERAGE_LAYOUT.md) includes a java implementation and explanation as well. [Peter Eades](https://en.wikipedia.org/wiki/Peter_Eades) brought the "Spring Embedder" model to help minimize crossover in edges and leveraged physical force based modelling to more naturally and evenly disperse nodes.

But perhaps most relevant of these contributions are those of Fruchterman-Reingold, whose introduction of a global temperature. This addition, another solution borrowing from the physical phenomenon of [annealing](https://en.wikipedia.org/wiki/Annealing_(materials_science)), helps limit node movement as the layout iterates over the solution to help reach a stable solution.

## Java Fruchterman-Reingold Example

```java
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FruchtermanReingoldLayout {
    private Map<Integer, Point2D> positions;
    private double width;
    private double height;
    private Random random = new Random();

    public FruchtermanReingoldLayout(double width, double height) {
        this.width = width;
        this.height = height;
        positions = new HashMap<>();
    }

    public void initializePositions(int[] nodes) {
        for (int node : nodes) { // nodes are initialized randomly, the 'big bang'
            double x = random.nextDouble() * width;
            double y = random.nextDouble() * height;
            positions.put(node, new Point2D.Double(x, y));
        }
    }

    public void layout(int[] nodes, int[][] edges, int iterations) {
        double area = width * height;
        double k = Math.sqrt(area / nodes.length); // force scaling factor based on total space available
        double t = width / 10; // Initial temperature

        for (int it = 0; it < iterations; it++) {
            Map<Integer, Point2D> displacement = new HashMap<>(); // stores all the calculated movement coords
            
            for (int v : nodes) { // calculate node displacements - the repulsive force between the nodes themselves
                Point2D disp = new Point2D.Double(0, 0);
                for (int u : nodes) {
                    if (u != v) {
                        Point2D posV = positions.get(v);
                        Point2D posU = positions.get(u);
                        double dx = posV.getX() - posU.getX();
                        double dy = posV.getY() - posU.getY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double force = k * k / distance;
                        disp.setLocation(disp.getX() + (dx / distance) * force, disp.getY() + (dy / distance) * force);
                    }
                }
                displacement.put(v, disp);
            }

            for (int[] edge : edges) { // calculate edge displacements - the attractive foce acting bringing related nodes together
                int v = edge[0];
                int u = edge[1];
                Point2D posV = positions.get(v);
                Point2D posU = positions.get(u);
                double dx = posV.getX() - posU.getX();
                double dy = posV.getY() - posU.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double force = distance * distance / k;
                Point2D dispV = displacement.get(v);
                Point2D dispU = displacement.get(u);
                dispV.setLocation(dispV.getX() - (dx / distance) * force, dispV.getY() - (dy / distance) * force);
                dispU.setLocation(dispU.getX() + (dx / distance) * force, dispU.getY() + (dy / distance) * force);
                displacement.put(v, dispV);
                displacement.put(u, dispU);
            }

            for (int v : nodes) { // applying displacements to correlated nodes
                Point2D disp = displacement.get(v);
                Point2D posV = positions.get(v);
                double dx = disp.getX();
                double dy = disp.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double limitedDist = Math.min(distance, t);
                posV.setLocation(posV.getX() + (dx / distance) * limitedDist, posV.getY() + (dy / distance) * limitedDist);
                positions.put(v, posV);
            }

            t *= 0.95; // Cool down, thanks Fruchterman-Reingold!
        }
    }
}

```
## JUNG Layouts' Different Approaches
Inside the [JUNG Library](https://github.com/jrtom/jung) there are two different implementations of the Fruchterman-Reingold layout algorithm. In examining these algorithims there's an opportunity to explore some Google (adjacent) code, test the differences, and get into the weeds of optimization choices. There are a few confusing details to iron out here on which implementation is exactly in use, what the differences are, and how the Fruchterman-Reingold layout algorithm actually works. 

- [On Github](https://github.com/jrtom/jung/blob/master/jung-algorithms/src/main/java/edu/uci/ics/jung/layout/algorithms/FRBHVisitorLayoutAlgorithm.java) there appears to be a completely different Barnes-Hut Quadtree optimized version existing under the `FRBHVisitorLayoutAlgorithm.java` class. It is definitley worth the look, with mostly minimal changes to the `calcReupulsion()` method, however it's unclear if this is available in the libraries 2.1 release.
- [The 2.1.1 docs](https://jrtom.github.io/jung/javadoc/index.html?overview-summary.html) reference a `FRLayout` and `FRLayout2` - this matches with my import and use of the library inside the project so will be the difference examined further.

## FRLayout vs. FRLayout2: Differences Summarized
  - `FRLayout2` implements handling frozen nodes differently from `FRLayout`, where the first implementation will skip updating pair's that are frozen, `FR2` allows for this one sided relationship by compensating in the calculated change for the unfrozen node. This happens across a few of the methods: `calcPositions() calcAttractions() calcRepulsion()` and from the comments and any reading I could find may improve the overall accuracy and visual appearance of the resultant layout.
  - `FR2` includes some movement constraint logic to limit 'big' jumps in the `calcPositions()` method, given the above optimization it seems these two almost act to balance each others effects across the algorithims methods. 
  - Small changes are made to the way boundaries, and boundary checking are handled. The includsion of a `Rectangle2D innerBounds` at the class variable level provides a class wide access to a means of checking boundaries haven't been exceeded by any member of the layout 
  - The inclusion of a `FRVertexData` class inside of `FRLayout` - `FR2` opts for a simpler implementation and data structure w/o the helpers for normalizing and offsetting which could help speed, but it's unclear if this is a convienence or optimization change. 

I ran some tests varying the size of Graph from 100 Nodes & 2000 Edges, up to 1500 Nodes & 190,000 Edges (approx.), and didn't find any tremendouse difference in performance gains between the implementations. The small but insignificant performance gain trend emerged as the Graph size increased, but the inconsistency in improvements don't provide any confidence the tests were even valid. The working hypothesis is this is due to my compute limitations. (Update: A change to the GraalVM did change how these performed see: [JUNG 3D FR Implements](dev/../JUNG_3D_FR_Impl.md) for more details)

## References

- Fruchterman-Reingold : plaintext copy, I have never actually typed that.
- [Googles Caches Explained](https://github.com/google/guava/wiki/CachesExplained)
- [Graph Drawing by Force Directed by Force-Directed Placement: Fruchterman-Reingold](docs/../references/Graph%20Drawing%20by%20Force%20Directed%20Placement.pdf)
- [Graphs Explained (by the Guava Team)](https://github.com/google/guava/wiki/GraphsExplained#choosing-the-right-graph-type)
- [FADE: Graph Drawing Clustering, and Visual Abstraction by Aaron quigley and Peter Eades](docs/../references/Fade-2000-aquigley.pdf)
- [Graph: an Overview Complete Handbook](docs/refe/graphs_handbook_full)
- [Force-Directed Graph Drawing Using Social Gravity and Scaling by Michael J. Bannister, David Eppstein, Michael T. Goodrich, Lowell Trott](docs/../references/1209.0748v1.pdf)
- [JUNG2 2.0 API Documentation (deprecated)](https://jung.sourceforge.net/doc/api/index.html): *contains some functionality not carried into 2.1*
- [JUNG2 2.1 API Documentation](https://jrtom.github.io/jung/javadoc/overview-summary.html)
- [Abstract Layout Docs](https://jrtom.github.io/jung/javadoc/edu/uci/ics/jung/algorithms/layout/AbstractLayout.html): *All Layouts inherit this - good place to start in the docs*
- [The Origins of Graph Theory](https://carolinabento.medium.com/the-origins-of-graph-theory-20d5c5dfda1) *fun historic context*


