# The Weighted Average Approach & A Bit of Context: W.T. Tuttle
The original idea for attempting to layout a graph relied on a relatively simlply idea, using weighted means to calculate the best fit for a 'centroid'. And with a little bit of algebra and the recursive brute force math engine that is a computer, is an applicable solution to the layout problem. The beauty of this solution (in my humble opinion) is in it's approachability and easily iterable complexity as an introduction to a number of other interesting fields like Graph Theory.    

William Thomas Tuttle is a mathmetician, code breaker and educator responsible for a number of breakthroughs in... let's go with Science & Engineering. His passion for learning and reasearch make it tremendously hard to narrow his contributions in relation to this application, especially given my limited understanding of his contributions (which span an almost comical number of fields), but - without a 'Tutte Polynomial' Graph Theory as a modern field would be very different. His work in the drawing, layout, and analysis of weighted graphs in particular are likely originating sources for much of the approach outlined below, and throughout this application. 

```java
public class GraphLayout {
    static class Node {
        String name;
        double[] position;

        public Node(String name) {
            this.name = name;
            this.position = new double[2]; // x, y positions
        }
    }

    static class Edge {
        Node to;
        double weight;

        public Edge(Node to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static void weightedMeansLayout() {
        Map<String, Node> nodes = new HashMap<>();
        Map<Node, List<Edge>> graph = new HashMap<>();

        // Create nodes
        nodes.put("A", new Node("A"));
        nodes.put("B", new Node("B"));
        nodes.put("C", new Node("C"));

        
        Random rand = new Random(); // init random positions...
        for (Node node : nodes.values()) {
            node.position[0] = rand.nextDouble();
            node.position[1] = rand.nextDouble();
        }

        // Create graph connections
        graph.put(nodes.get("A"), List.of(new Edge(nodes.get("B"), 2), new Edge(nodes.get("C"), 3)));
        graph.put(nodes.get("B"), List.of(new Edge(nodes.get("A"), 2), new Edge(nodes.get("C"), 4)));
        graph.put(nodes.get("C"), List.of(new Edge(nodes.get("A"), 3), new Edge(nodes.get("B"), 4)));

        // Main loop for position adjustment
        for (int i = 0; i < 100; i++) { // Run for 100 iterations
            for (Node node : nodes.values()) {
                double[] newPosition = {0, 0};
                double totalWeight = 0;

                for (Edge edge : graph.get(node)) {
                    newPosition[0] += edge.to.position[0] * edge.weight;
                    newPosition[1] += edge.to.position[1] * edge.weight;
                    totalWeight += edge.weight;
                }

                if (totalWeight > 0) {
                    node.position[0] = newPosition[0] / totalWeight;
                    node.position[1] = newPosition[1] / totalWeight;
                }
            }
        }
    }
}
```

# Key Takeaways:

This approach suffers from a lack of stability - with no controls in place each Node is freely able to traverse the layout any distance on any iteration. Then at the same time it has the opposite problem where it's likely to find a stable solution early and become stuck with a relatively bad layout. More modern approaches include cooling factors, repulsive forces, and convergence criteria to improve layouts and prevent issues known to cause bad solutions. There are also some glaringly lacking functionality limitation issues inside this graph which would be even more costly (computationally) to add to an already pretty expensive approach. 

That being said, this approach serves as an important stepping stone into the ideas, methods, and practices used to improve on the results. Broadly speaking, implementing something like a cooling factor with this as a starting point would require a simple definition for anyone to implement from this code as a starting point.

- **Cooling Factor:** A numerical representation of the overall "heat" of the system, where the cooler the system the smaller the (allowed) adjustments made to each node become. Assume this system cools non-linearly. 

A simple implementation of this adds no more than 5 lines of code in the main loop, with further obvious optimizations to be made simply around the degree cooling factor used:

# Simple Cooling Factor:

```java
  double temperature = 1.0; // Initial temperature
  double coolingFactor = 0.95; // Cooling factor

  // Main loop for position adjustment
  for (int i = 0; i < 100; i++) { // Run for 100 iterations
      for (Node node : nodes.values()) {
          double[] newPosition = {0, 0};
          double totalWeight = 0;

          for (Edge edge : graph.get(node)) {
              newPosition[0] += edge.to.position[0] * edge.weight;
              newPosition[1] += edge.to.position[1] * edge.weight;
              totalWeight += edge.weight;
          }

          if (total, weight > 0) {
              node.position[0] = (node.position[0] + temperature * (newPosition[0] / totalWeight)) / (1 + temperature);
              node.position[1] = (node.position[1] + temperature * (newPosition[1] / totalWeight)) / (1 + temperature);
          }
      }
      temperature *= coolingFactor; // Reduce the temperature
  }
```

Starting from a weighted means base and with 5 lines of code this optimization improves accuracy while adding no more mathmatic complexity then simple arithmatic. However the importance, at least in terms of thought process in applying "physical" principles to an otherwise not-physical system, cannot be overlooked! 