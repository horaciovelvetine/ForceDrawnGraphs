package edu.ForceDrawnGraphs.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public class SectionRecord {

  public final String name;
  public final String text;
  @JsonAlias("link_lengths")
  public final int[] linkLengths;
  @JsonAlias("link_offsets")
  public final int[] linkOffsets;
  @JsonAlias("target_page_ids")
  public final int[] targetPageIds;

  public SectionRecord() {
    this.name = "";
    this.text = "";
    this.linkLengths = new int[0];
    this.linkOffsets = new int[0];
    this.targetPageIds = new int[0];
  }

  public SectionRecord(String name, String text, int[] linkLengths, int[] linkOffsets, int[] targetPageIds) {
    this.name = name;
    this.text = text;
    this.linkLengths = linkLengths;
    this.linkOffsets = linkOffsets;
    this.targetPageIds = targetPageIds;
  }
}
