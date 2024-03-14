package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LinkAnnotatedTextRecord {

  @JsonAlias("page_id")
  public final int pageId;
  public final List<SectionRecord> sections;

  public LinkAnnotatedTextRecord() {
    this.pageId = 0;
    this.sections = new ArrayList<>();
  }

  public LinkAnnotatedTextRecord(int pageId, List<SectionRecord> sections) {
    this.pageId = pageId;
    this.sections = sections;
  }
}
