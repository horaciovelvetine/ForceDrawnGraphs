package edu.ForceDrawnGraphs.Wikiverse.models;

public class GSEdge {
  private int id;
  private long srcEntId;
  private long tgtEntId;
  private long srcRecordLine;
  private String srcRecordFile;
  private int weight;

  public GSEdge(long srcEntId, long tgtEntId, long srcRecordLine, String srcRecordFile) {

    // TODO: default implement of the edge
  }

  private void checkEntReferencesAreValid(long srcEntId, long tgtEntId) {
    // TODO: Should check that src and tgt are not the same, then set those values
  }

  public void setSrcTgtEntReferences(long srcEntId, long tgtEntId) {
    // ? this could need to be tracking also the types
  }

}
