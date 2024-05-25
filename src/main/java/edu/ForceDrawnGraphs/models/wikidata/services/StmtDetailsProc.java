package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.models.wikidata.records.VSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.records.ValueSnakRec;

class StmtDetailsProcessor {
  // visitors
  private VSnakVisitor snakVisitor = new VSnakVisitor();
  // private VValueVisitor valueVisitor = new VValueVisitor();
  // actual values
  private Statement originalStmt;
  private ValueSnakRec mainSnak;
  private List<ValueSnakRec[]> qualifiers;
  private Map<Integer, List<ValueSnakRec[]>> references;

  public StmtDetailsProcessor(Statement statement) {
    this.originalStmt = statement;
    this.mainSnak = setMainSnakDetails();
    this.qualifiers = setQualifierDetails();
    this.references = setReferencesDetails();
  }

  /**
   * Returns true if the Statement defines some sort of information sourced externally from Wikidata using one of the following criteria:
   * <ul>
     * <li> mainSnak is null</li>
     * <li> mainSnak's datatype is "external-id"</li>
     * <li> mainSnak's datatype is "monolingualtext"</li>
     * <li> mainSnak property is part of an internal exclusion list</li>
     * <li> qualifier properties are part of an internal exclusion list</li>
   * </ul>
   * 
   * @see (docs/PROPERTY_EXCLUSION_LIST.md) for a list of excluded properties
   */
  public boolean definesIrrelevantOrExternalInfo() {
    if (mainSnak == null)
      return true;
    if (mainSnak.datatype().equals("external-id"))
      // external-id defines a subject - irrelevant
      return true;
    if (mainSnak.datatype().equals("monolingualtext"))
      // text defines how to sepll a subject - irrelevant
      return true;
    if (externallySourcedProperty(mainSnak))
      // known list of irrelevant properties
      return true;
    if (externallySourcedProperty(qualifiers))
      // qualifiers are irrelevant properties
      return true;
    else
      return false;
  }

  public List<Edge> createEdgesFromDetails(Vertex srcVertex) {
    // first check mainSnak for a valid target
    // then (possibly if theres a valid target only) check qualifiers for additional valid target
    return null;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------
  /**
   * Checks if the property of the suspected Snak is part of an internal black list of externally sourced properties.
   */
  private boolean externallySourcedProperty(ValueSnakRec suspectedSnak) {
    String[] extPropQIDBlacklist = { "P1343", "P143", "P935", "P8687", "P3744", "P18", "P373" };
    for (String qid : extPropQIDBlacklist) {
      if (suspectedSnak.propertyIdValue().getId().equals(qid)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the property of the suspected List is part of an internal black list of externally sourced properties.
   */
  private boolean externallySourcedProperty(List<ValueSnakRec[]> suspectedList) {
    if (suspectedList != null) {
      for (ValueSnakRec[] snakGroup : suspectedList) {
        for (ValueSnakRec snak : snakGroup) {
          if (externallySourcedProperty(snak)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Collects the details of the main Snak of the original statement.
   */
  private ValueSnakRec setMainSnakDetails() {
    return originalStmt.getMainSnak().accept(snakVisitor);
  }

  /**
   * Collects the details of the qualifiers of the original statment.
   */
  private List<ValueSnakRec[]> setQualifierDetails() {
    return collectSnakGroupedDetails(originalStmt.getQualifiers());
  }

  /**
   * Collects the details of each Snak in a SnakGroup and returns them as an array of ValueSnakRecs,
   * where each array represents a distinct SnakGroup, then maps the groups using their index for a key.  
   */
  private Map<Integer, List<ValueSnakRec[]>> setReferencesDetails() {
    Map<Integer, List<ValueSnakRec[]>> refDetailMap = new HashMap<>();

    for (int i = 0; i < originalStmt.getReferences().size(); i++) {
      Reference reference = originalStmt.getReferences().get(i);

      List<SnakGroup> refGroups = reference.getSnakGroups();
      List<ValueSnakRec[]> refGroupDetails = collectSnakGroupedDetails(refGroups);

      if (refGroupDetails != null) {
        refDetailMap.put(i, refGroupDetails);
      }
    }
    return refDetailMap.isEmpty() ? null : refDetailMap;
  }

  /**
   * Collects the details of each Snak in a SnakGroup and returns them as an array of ValueSnakRecs,
   * where each array represents a distinct SnakGroup.
   */
  private List<ValueSnakRec[]> collectSnakGroupedDetails(List<SnakGroup> groups) {
    List<ValueSnakRec[]> snakGroupedDetails = new ArrayList<>();

    for (SnakGroup group : groups) {
      ValueSnakRec[] groupDetails = new ValueSnakRec[group.size()];

      for (Snak snak : group) {
        ValueSnakRec snakDetails = snak.accept(snakVisitor);
        if (snakDetails != null) {
          groupDetails[group.getSnaks().indexOf(snak)] = snakDetails;
        }
      }
      snakGroupedDetails.add(groupDetails);
    }

    return snakGroupedDetails.isEmpty() ? null : snakGroupedDetails;
  }

}