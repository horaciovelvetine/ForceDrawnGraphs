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
import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

@Component
public class LocalDatabase implements Loggable {
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
  private final WikisetDao wikisetDao = new WikisetDao(dbConnection);

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

  private void executeSqlScript(String sqlFilePath) throws IOException {
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

  public Wikiset findOrCreateWikiset() {
    try {
      executeSqlScript("sql/CreateWikisetTable.sql");
      return wikisetDao.findOrCreateWikiset();
    } catch (IOException e) {
      log(e);
      return null;
    }
  }

}
