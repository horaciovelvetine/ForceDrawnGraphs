package edu.ForceDrawnGraphs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Property;
import edu.ForceDrawnGraphs.models.Vertex;

public class FirstResponder {
  // as an homage to those who respond first, I bravely trudge on.
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String STORE_DIR = "src/main/resources/data/"; // to keep a testable consistent JSON copy of the Data

  public static void serializeGraphset(Graphset graphset) {
    try {
      mapper.writerWithDefaultPrettyPrinter()
          .writeValue(new File(STORE_DIR + "graphset-full" + ".json"), graphset);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Graphset desrializeResponset() {
    try {
      return mapper.readValue(new File(STORE_DIR + "graphset-full.json"), Graphset.class);
    } catch (FileNotFoundException e) {
      System.out.println(
          "deserializeResponset(): No Graphset file found locally, run the `ig` command to create one.");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new Graphset();
  }

  public static void createResponse(Graphset graphset) {
    try {
      ResponseV1 response = new ResponseV1(graphset);
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File(STORE_DIR + "response.json"),
          response);
    } catch (Exception e) {
      System.out.println("createResponse()::" + e.getMessage());
    }
  }

  private static class ResponseV1 {
    public Set<Vertex> vertices;
    public Set<Edge> edges;
    public Set<Property> properties;
    public String originQuery;

    public ResponseV1(Graphset graphset) {
      this.vertices = graphset.getFetchCompleteWikidataVertices();
      this.edges = graphset.getFetchCompleteEdges();
      this.properties = graphset.properties();
      this.originQuery = graphset.originQuery();
    }
  }

}
