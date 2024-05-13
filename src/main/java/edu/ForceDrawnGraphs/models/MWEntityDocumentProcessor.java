package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class MWEntityDocumentProcessor implements Reportable {
  /**
   * Default Constructor.
   */
  public MWEntityDocumentProcessor() {
    //Default
  }

  private void processItemDocument(ItemDocument document) {
    // Handle our items here
    Vertex vertex = new Vertex(document); // create a new vertex for the item document

    Iterator<Statement> statements = document.getAllStatements(); // sometimes this is 0

    while (statements.hasNext()) {
      Statement statement = statements.next();

      if (statement.getMainSnak() instanceof ValueSnak) { // checking if the main snak has any value
        Value mainSnakValue = ((ValueSnak) statement.getMainSnak()).getValue(); // get the value of the main snak

        if (!(mainSnakValue instanceof ItemIdValue)) { // checking if this is a statement that points to another item
          continue; // if not, we can skip this statement
        }
        ItemIdValue mainSnakItemId = (ItemIdValue) mainSnakValue; // get the item id value of the main snak
        String mainSnakQID = statement.getMainSnak().getPropertyId().getId(); // edge.mwStmntQID
        List<Reference> references = statement.getReferences(); // 

        if (references.size() > 0) {
          print("References found");
        }
        
        //! Stopped here
        // The above seems like a good approach, clearly type narrowing here will give access to the correct methods, and help weed out the incorrect ones. THen helpers can be made to handle the different inputs, which can be cast as the narrowed type.


        for (SnakGroup group : statement.getQualifiers()) {
          for (Snak snak : group.getSnaks()) {
            String qualifierQID = snak.getPropertyId().getId();
            if (snak instanceof ValueSnak) {
              Value value = ((ValueSnak) snak).getValue();
              String qualifierValue = value.toString();
            } else {
              print("Non-ValueSnak found");
            }
          }
        }
        print("Process Item");
      } else if (statement.getMainSnak() instanceof NoValueSnak) {
        print("Non-ValueSnak found");
      } else {
        print("Main snak type unhandled.");
      }
    }

  }

  /**
   * Ingests and handles the return results when querying the MediaWiki API by passing the entity document to the correct processor based on the type of document.
   */
  public void ingestEntityDocument(EntityDocument document) {
    if (document instanceof ItemDocument) {
      processItemDocument((ItemDocument) document);
    } else {
      report("Encountered new DocType: " + document.getClass().getName());
    }
  }
}
