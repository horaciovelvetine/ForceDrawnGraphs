package edu.ForceDrawnGraphs.Wikiverse.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ForceDrawnGraphs.Wikiverse.exceptions.LocalDatabaseConnectionException;

@Component
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
  // DB Config params
  public static final String DB_NAME = "en_Wikipedia_page_text_12_01_2019"; // ==> MATCHES LOCAL PG DB INSTANCE
  private static final String USER = "postgres";
  private static final String PASS = "";
  private static final String URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
  // Connection Objects & Utils...
  private final BasicDataSource dataSource = new BasicDataSource() {
    {
      setUsername(USER);
      setPassword(PASS);
      setUrl(URL);
    }
  };
  private final JdbcTemplate dbConnection = new JdbcTemplate(dataSource);
  private final ObjectMapper objectMapper = new ObjectMapper();

  public LocalDatabase() {
    checkConnectionIsValid();
  }

  public void checkConnectionIsValid() throws LocalDatabaseConnectionException {
    String sql = "SELECT * FROM valid_connection";
    SqlRowSet results = dbConnection.queryForRowSet(sql);
    if (results.next()) {
      System.out.println(results.getString("valid_message"));
    } else {
      throw new LocalDatabaseConnectionException("Local DB Connection Failed!");
    }
  }

  public void executeSqlScript(String sqlFilePath) throws IOException {
    Resource resource = new ClassPathResource(sqlFilePath);
    byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
    String sqlScript = new String(data, StandardCharsets.UTF_8);

    // Split by semicolon and remove empty lines
    String[] sqlStatements = sqlScript.split(";\\s*\\r?\\n");
    for (String sqlStatement : sqlStatements) {
      if (sqlStatement.trim().length() > 0) {
        dbConnection.execute(sqlStatement);
      }
    }
  }

  @Override
  public String toString() {
    return "LocalDatabase [BASE_PATH=" + BASE_PATH + ", ITEM_ALIASES_PATH=" + ITEM_ALIASES_PATH
        + ", ITEM_PATH=" + ITEM_PATH + ", LINK_ANNOTATED_TEXT_PATH=" + LINK_ANNOTATED_TEXT_PATH
        + ", PAGE_PATH=" + PAGE_PATH + ", PROPERTY_ALIASES_PATH=" + PROPERTY_ALIASES_PATH
        + ", PROPERTY_PATH=" + PROPERTY_PATH + ", STATEMENTS_PATH=" + STATEMENTS_PATH + ", DB_NAME="
        + DB_NAME + ", USER=" + USER + ", PASS=" + PASS + ", URL=" + URL + "]";
  }
}
