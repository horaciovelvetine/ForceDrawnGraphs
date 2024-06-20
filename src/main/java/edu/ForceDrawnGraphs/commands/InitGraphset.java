package edu.ForceDrawnGraphs.commands;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import javax.swing.JFrame;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.jung.GraphsetDecorator;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.util.ProcessTimer;
import edu.ForceDrawnGraphs.wikidata.services.APIBroker;
import edu.ForceDrawnGraphs.wikidata.services.EntDocProc;

import edu.uci.ics.jung.algorithms.layout.FRLayout;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

@ShellComponent
public class InitGraphset implements Reportable {
  // Wikidata
  private static Graphset graphset = new Graphset();
  private static EntDocProc docProc = new EntDocProc(graphset);
  private static APIBroker wikidataAPI = new APIBroker(docProc, graphset);
  //JUNG
  private static GraphsetDecorator graphsetDec = new GraphsetDecorator(graphset);
  // Configuration
  private static Integer targetDepth = 2;
  private static Dimension graphSize = new Dimension(3200, 1600); // from cx by default? 

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("InitGraphset()::");

    graphset.setOriginQuery(target);
    wikidataAPI.fuzzyFetchOriginEntityDocument(target);

    // graphsetDec.addGraphEventListener(event -> {
    //   print(event.toString());
    // });

    while (graphset.depth() <= targetDepth) {
      wikidataAPI.fetchQueuedValuesDetails();
      graphsetDec.addCompleteWikidataEnts();

      if (!graphset.wikiDataFetchQueue().hasItems(graphset.depth())) {
        graphset.iterateNDepth();
      }

      report(graphset.toString() + "\n[Vertices: " + graphsetDec.getVertexCount() + " Edges: "
          + graphsetDec.getEdgeCount() + "]\n");
      timer.lap();
    }

    //TODO - end of the fetch (sh?)could close the Vertex data (by fetching remaining ItemDocs)
    //TODO - have the edges look up labels before adding to the graphsetDec

    timer.end();

    ProcessTimer timer2 = new ProcessTimer("init FRLatout()::");
    FRLayout<Vertex, Edge> layout = null;
    try {
      layout = new FRLayout<Vertex, Edge>(graphsetDec, graphSize);
      layout.initialize();
      layout.setRepulsionMultiplier(1.2);
      while (!layout.done()) {
        layout.step();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    timer2.end();

    // Visualization code starts here
    VisualizationViewer<Vertex, Edge> vv = new VisualizationViewer<>(layout);
    vv.setPreferredSize(new Dimension(850, 850)); // Set the viewing area size
    // vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    // vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

    // Set up tooltips
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

    StringBuilder sb = new StringBuilder();
    for (Vertex v : graphsetDec.getVertices()) {
      Point2D p = layout.apply(v);
      sb.append("\n" + v.toString() + "\n" + p.toString() + "\n");
    }
    report(sb.toString());

    print("stop");
  }
}
