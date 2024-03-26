package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class LinkAnnotatedTextRecord implements Reportable {

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

  public LinkAnnotatedTextRecord createLATRFromStringData(String stringifiedJSON) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(stringifiedJSON, LinkAnnotatedTextRecord.class);

    } catch (Exception e) {
      report(e);
    }
    return null;
  }
}
