package edu.ForceDrawnGraphs.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LinkAnnotatedTextRecord {
  @JsonAlias("pageId")
  public final int pageId;
  public final List<SectionRecord> sections;

  public LinkAnnotatedTextRecord(int pageId, List<SectionRecord> sections) {
    this.pageId = pageId;
    this.sections = sections;
  }

}
