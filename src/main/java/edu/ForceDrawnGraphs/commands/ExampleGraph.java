package edu.ForceDrawnGraphs.commands;

import javax.swing.JFrame;
import java.awt.Dimension;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

@ShellComponent
public class ExampleGraph {

  @ShellMethod("Example Graph Testing")
  public void exGraph() {

    DirectedSparseMultigraph<String, Integer> graph = new DirectedSparseMultigraph<>();

    // Step 2: Add some nodes and edges
    graph.addVertex("A");
    graph.addVertex("B");
    graph.addVertex("C");
    graph.addEdge(1, "A", "B");
    graph.addEdge(2, "B", "C");
    graph.addEdge(3, "C", "A");


    // Step 3: Set up the layout
    FRLayout<String, Integer> layout = new FRLayout<>(graph);
    layout.setSize(new Dimension(300, 300)); // dimensions must be set

    System.out.println("Stop graph");

  }

}
