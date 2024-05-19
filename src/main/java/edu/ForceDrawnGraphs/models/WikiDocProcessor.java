package edu.ForceDrawnGraphs.models;

import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.WikiDocStmtPropertyDetails.SnakSrcType;

/**
 * A class to process the entity documents returned from the MediaWiki API and interact with the main Graphset.
 * 
 * @param graphset Graphset notified of all object updates and changes.
 */
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
    * Ingests Entity Document results from the Wikimedia API, type narrows, and directs them to the appropriate processing method.
    * For documents it does not recognize, it logs a message containing that type and throws an error to allow for handling. 
    *
    * @param document the EntityDocument to be ingested
    * @param isOrigin a boolean flag to indicate if the document is the origin document
   */
  public void ingestEntityDocument(EntityDocument document) {
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
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Uses an ItemDocument to create a new Vertex which is added to the Graphset.
   * Continues to iterate over the ItemDocument's statements, processing each one to create edges in the Graphset. 
   * Edges connect to more vertices, which are passed to the WikiDocFetchQueue for lookup restarting the process.
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private void ingestItemDocForGraphset(ItemDocument itemDoc) {
    ProcessTimer timer = new ProcessTimer(
        "processItemDocument(" + itemDoc.getEntityId() + ") in WikiDocProcessor.java");
    Vertex vertex = new Vertex(itemDoc); // create a new vertex for the item document
    graphset.addVertex(vertex); // add the vertex to the graphset

    String srcVertexQID = itemDoc.getEntityId().getId(); // get the QID of the item document
    procItemStatementsForEdges(itemDoc, srcVertexQID); // check for and process statement edges...
    //TODO - Created edges arent fetching the data for their target vertices, need to add them to the fetch queue
    timer.end();
  }

  /**
   * Processes each of the statements, claims, qualifiers, and references of an ItemDocument creating edges for each relevant Snak.
   * 
   * @param itemDoc the ItemDocument to be processed
   * @param srcVertexQID the QID of the vertex created from the ItemDocument
   */
  private void procItemStatementsForEdges(ItemDocument itemDoc, String srcVertexQID) {
    Iterator<Statement> statements = itemDoc.getAllStatements();

    while (statements.hasNext()) {
      //TODO - Add a check for the statement's rank, determine relevant?
      Statement statement = statements.next();
      procSnakTypeForEdge(statement.getMainSnak(), srcVertexQID, SnakSrcType.CLAIM, statement); // process the main snak for the statement
      //
      if (statement.getQualifiers() != null) {
        procSnakGroupListForEdges(statement.getQualifiers(), srcVertexQID, SnakSrcType.QUALIFIER, statement); // process the (if) qualifiers for the statement
      }

      if (statement.getReferences().size() != 0) { // process the (if) references for the statement
        for (Reference reference : statement.getReferences()) {
          procSnakGroupListForEdges(reference.getSnakGroups(), srcVertexQID, SnakSrcType.REFERENCE, statement);
        }
      }
      // No more statements to process, move on to the next one...
    }
  }

  /**
   * Processes a list of SnakGroups, each containing a list of Snaks then pass each to procSnakForEdge().
   * 
   * @param snakGroups the list of SnakGroups to be processed
   * @param srcVertexQID the QID of the vertex created from the ItemDocument
   * @param stmtSrcTypeEnum the type of Snak source
   * @param statement the Statement the Snak is a part of
   */
  private void procSnakGroupListForEdges(List<SnakGroup> snakGroups, String srcVertexQID, SnakSrcType stmtSrcTypeEnum,
      Statement statement) {
    // Each member of a SnakGroup is a Snak which shares the same parent property.
    for (SnakGroup snakGroup : snakGroups) {
      for (Snak snak : snakGroup) {
        procSnakTypeForEdge(snak, srcVertexQID, stmtSrcTypeEnum, statement);
      }
    }
  }

  /**
   * Determines the type of Snak through type narrowing to ignore irrelevant info, then cast the Snak to the appropriately narrowed type for processing, to the ingestValueSnakAsEdge() method.
   * 
   * @param snak the Snak to be processed
   * @param srcVertexQID the QID of the vertex created from the ItemDocument
   * @param stmtSrcTypeEnum the type of Snak source
   * @param statement the Statement the Snak is a part of
   */
  private void procSnakTypeForEdge(Snak snak, String srcVertexQID, SnakSrcType stmtSrcTypeEnum, Statement statement) {
    if (snak instanceof NoValueSnak || snak instanceof SomeValueSnak) {
      return; // These contain no values and can be skipped...
    } else if (snak instanceof ValueSnak) {
      procValueSnakForEntityQIDValue((ValueSnak) snak, srcVertexQID, stmtSrcTypeEnum, statement);
    } else {
      throw new IllegalArgumentException(
          "Unhandled Snak type @ procSnakForEdge(): " + snak.getClass().getName());
    }
  }

  /**
   * Determines the type of EntityIdValue through type narrowing to ignore irrelevant info, then cast the EntityIdValue to the appropriately narrowed type for processing, to the ingestEntValueSnakAsEdge() method.
   * 
   * @param snak the ValueSnak to be processed
   * @param srcVertexQID the QID of the vertex created from the ItemDocument
   * @param stmtSrcTypeEnum the type of Snak source
   * @param statement the Statement the Snak is a part of
   */
  private void procValueSnakForEntityQIDValue(ValueSnak snak, String srcVertexQID, SnakSrcType stmtSrcTypeEnum,
      Statement statement) {
    if (snak.getValue() instanceof EntityIdValue) {
      ingestEntValueSnakAsEdge(snak, srcVertexQID, stmtSrcTypeEnum, statement);
    } else if (snak.getValue() instanceof PropertyIdValue) {
      // This should not happen, tell us if it does...
      print("PropertyQID found in ValueSnak. @ toString(): " + snak.getValue());
    } else if (snak.getValue() instanceof TimeValue) {
      // TODO: Time Values entry point - using title (the year?) search for the ItemDocument representing that year
      print("TimeValue found in ValueSnak. @ toString(): " + snak.getValue());
    } else {
      report("Ignored value type: " + snak.getValue().getClass().getName() + ". @ toString(): "
          + snak.getValue());
    }
  }

  private void ingestEntValueSnakAsEdge(ValueSnak entValueSnak, String srcVertexQID,
      SnakSrcType stmtSrcTypeEnum,
      Statement statement) {
    WikiDocStmtPropertyDetails stmtDetails = createStmtDetails(entValueSnak, statement, stmtSrcTypeEnum);
    String tgtVertexQID = ((EntityIdValue) entValueSnak.getValue()).getId();

    Edge edge = new Edge(srcVertexQID, tgtVertexQID, stmtDetails);
    print("stop here");
  }

  private WikiDocStmtPropertyDetails createStmtDetails(ValueSnak entValueSnak, Statement srcStmt,
      SnakSrcType snakSrcType) {
    String propTypeQID = entValueSnak.getPropertyId().getId();

    // TODO: Source of double quotation issues and raw toString() call
    String propValue = (((ValueSnak) srcStmt.getMainSnak()).getValue()).toString(); 
    return new WikiDocStmtPropertyDetails(propTypeQID, propValue, snakSrcType);
  }
}
