package edu.ForceDrawnGraphs.Wikiverse.db;

public class LocalDatabase {
  // Local data files...
  public static final String BASE_PATH = "src/main/resources/data";
  public static final String ITEM_ALIASES_PATH = BASE_PATH + "/item_aliases.csv";
  public static final String ITEM_PATH = BASE_PATH + "/item.csv";
  public static final String LINK_ANNOTATED_TEXT_PATH = BASE_PATH + "/link_annotated_text.jsonl";
  public static final String PAGE_PATH = BASE_PATH + "/page.csv";
  public static final String PROPERTY_ALIASES_PATH = BASE_PATH + "/property_aliases.csv";
  public static final String PROPERTY_PATH = BASE_PATH + "/property.csv";
  public static final String STATEMENTS_PATH = BASE_PATH + "/statements.csv";
  // Config params
  public static final String DB_NAME = "en_Wikipedia_page_text_12_01_2019"; // ==> MATCHES LOCAL PG DB INSTANCE
  private static final String USER = "postgres";
  private static final String PASS = "";
  private static final String URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
  private static final String DRIVER = "org.postgresql.Driver";

  public LocalDatabase() {
    System.out.println(this.toString());
  }

  @Override
  public String toString() {
    return "LocalDatabase [BASE_PATH=" + BASE_PATH + ", ITEM_ALIASES_PATH=" + ITEM_ALIASES_PATH
        + ", ITEM_PATH=" + ITEM_PATH + ", LINK_ANNOTATED_TEXT_PATH=" + LINK_ANNOTATED_TEXT_PATH
        + ", PAGE_PATH=" + PAGE_PATH + ", PROPERTY_ALIASES_PATH=" + PROPERTY_ALIASES_PATH
        + ", PROPERTY_PATH=" + PROPERTY_PATH + ", STATEMENTS_PATH=" + STATEMENTS_PATH + ", DB_NAME="
        + DB_NAME + ", USER=" + USER + ", PASS=" + PASS + ", URL=" + URL + ", DRIVER=" + DRIVER
        + "]";
  }
}
