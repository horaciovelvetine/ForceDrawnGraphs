package edu.ForceDrawnGraphs.Wikiverse.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;


@Component
public class Database {
  // Local data file paths:
  public static final String BASE_PATH = "src/main/java/edu/ForceDrawnGraphs/Wikiverse/data";
  public static final String ITEM_ALIASES_PATH = BASE_PATH + "/item_aliases.csv";
  public static final String ITEM_PATH = BASE_PATH + "/item.csv";
  public static final String LINK_ANNOTATED_TEXT_PATH = BASE_PATH + "/link_annotated_text.jsonl";
  public static final String PAGE_PATH = BASE_PATH + "/page.csv";
  public static final String PROPERTY_ALIASES_PATH = BASE_PATH + "/property_aliases.csv";
  public static final String PROPERTY_PATH = BASE_PATH + "/property.csv";
  public static final String STATEMENTS_PATH = BASE_PATH + "/statements.csv";
  // Local PG DATABASE Details & Credentials:
  public static final String DB_NAME = "en_Wikipedia_page_text_12_01_2019"; // ==> MATCHES LOCAL PG DB INSTANCE
  private static final String USER = "postgres";
  private static final String PASS = "";
  private static final String URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
  private static final String DRIVER = "org.postgresql.Driver";
  // Local PG DATABASE Connection Pool:
  private BasicDataSource connection = new BasicDataSource();

  public Database() {
    connection.setDriverClassName(DRIVER);
    connection.setUrl(URL);
    connection.setUsername(USER);
    connection.setPassword(PASS);
  }

  public BasicDataSource getConnection() {
    return connection;
  }
}
