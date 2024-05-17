package edu.ForceDrawnGraphs.models;

import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.interfaces.Reportable;

public class WikiDocProcessor implements Reportable {
  private Graphset graphset;

  /**
   * A class to process the entity documents returned from the MediaWiki API and interact with the main Graphset.
   * 
   * @param graphset Graphset notified of all object updates and changes.
   */
  public WikiDocProcessor(Graphset graphset) {
    this.graphset = graphset;
  }

  /**
    * Shortcut method to ingestEntityDocument(ent, false) defaulting isOrigin() boolean to false.
    *
    * @param document the EntityDocument to be ingested
    * @see ingestEntityDocument(EntityDocument document, boolean isOrigin)
   */
  public void ingestEntityDocument(EntityDocument document) {
    ingestEntityDocument(document, false);
  }

  /**
    * Ingests Entity Document results from the Wikimedia API, type narrows, and directs them to the appropriate processing method.
    * For documents it does not recognize, it logs a message containing that type and throws an error to allow for handling. 
    *
    * @param document the EntityDocument to be ingested
    * @param isOrigin a boolean flag to indicate if the document is the origin document
   */
  public void ingestEntityDocument(EntityDocument document, boolean isOrigin) {
    // TODO: Decide how/where the origin flag is used
    // TODO: Is switch statement more appropriate here?

    if (document instanceof ItemDocument) {
      ingestItemDocForGraphset((ItemDocument) document); // process and add the ItemDoc to the graphset
    } else {
      report("Encountered new DocType: " + document.getClass().getName());
      throw new IllegalArgumentException("Unhandled EntityDocument type: " + document.getClass().getName());
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private void ingestItemDocForGraphset(ItemDocument itemDoc) {
    ProcessTimer timer = new ProcessTimer(
        "processItemDocument(" + itemDoc.getEntityId() + ") in WikiDocProcessor.java");

    Vertex vertex = new Vertex(itemDoc); // create a new vertex for the item document
    graphset.addVertex(vertex); // add the vertex to the graphset

    procItemStatementsForEdges(itemDoc, vertex.getQID()); // check for and process statement edges...
    timer.end();
  }

  private void procItemStatementsForEdges(ItemDocument itemDoc, String srcVertexQID) {
    Iterator<Statement> statements = itemDoc.getAllStatements();

    while (statements.hasNext()) {
      Statement statement = statements.next();
      // Stuff we need to look through for EntityIdValues
      Snak mainSnak = statement.getMainSnak();
      List<SnakGroup> qualifiers = statement.getQualifiers();
      List<Reference> refs = statement.getReferences(); // next step is to iterate over the references and call getSnakGroups() --> this will similarly return a list of SnakGroups
    }
  }

  private void procSnakForEdge(Snak snak, Vertex srcVertex, String stmtSrcTypeEnum) {
    // TODO: stmtSrcType is a string that should be an enum for a TBD class to wrap the details of the statement source

    if (snak instanceof NoValueSnak || snak instanceof SomeValueSnak) {
      return; // These contain no values and can be skipped...
    } else if (snak instanceof ValueSnak) {
      toBeNamedValueSnakMethod((ValueSnak) snak);
    } else {
      report("Encountered new SnakType: " + snak.getClass().getName());
      throw new IllegalArgumentException("Unhandled Snak type: " + snak.getClass().getName());
    }
  }

  private void toBeNamedValueSnakMethod(ValueSnak valueSnak) {
  }
}
