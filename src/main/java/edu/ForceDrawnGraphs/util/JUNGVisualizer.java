package edu.ForceDrawnGraphs.util;

import javax.swing.JFrame;

import java.awt.Dimension;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

// ==> Quick visualizer for sanity checks on layouts <==
public class JUNGVisualizer {
  public void visualizeGraph(Layout<Vertex, Edge> layout) {

    VisualizationViewer<Vertex, Edge> vv = new VisualizationViewer<>(layout);
    vv.setPreferredSize(new Dimension(1600, 900)); //viewing area size

    vv.setVertexToolTipTransformer(vertex -> "Vertex Info: " + vertex.toString());
    vv.setEdgeToolTipTransformer(edge -> "Edge Info: " + edge.toString());

    DefaultModalGraphMouse<Vertex, Edge> graphMouse = new DefaultModalGraphMouse<>();
    graphMouse.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    vv.setGraphMouse(graphMouse);

    JFrame frame = new JFrame("Graph Visualization");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(vv);
    frame.pack();
    frame.setVisible(true);
  }
}
