package edu.ForceDrawnGraphs.util;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ForceDrawnGraphs.models.Graphset;

public class FirstResponder {
  // as an homage to those who respond first, I bravely trudge on.
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String STORE_DIR = "src/main/resources/data/"; // to keep a testable consistent JSON copy of the Data

  public static void serializeResponset(Graphset graphset) {
    try {
      mapper.writeValue(new File(STORE_DIR + "testset-n" + graphset.depth() + ".json"), graphset);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Graphset desrializeResponset() {
    try {
      return mapper.readValue(new File(STORE_DIR + "testset-n4.json"), Graphset.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
