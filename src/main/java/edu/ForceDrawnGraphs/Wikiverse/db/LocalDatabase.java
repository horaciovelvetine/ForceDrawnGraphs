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

  public void createWikiset() {
    try {
      executeSqlScript("sql/CreateWikisetTable.sql");
      wikisetDao.createWikiset();
    } catch (IOException e) {
      log(e);
    }
  }

  public Wikiset findOrCreateWikiset() {
    Wikiset wikiset = wikisetDao.getWikiset();
    if (wikiset == null) {
      print("Wikiset not found! Creating a wikiset...");
      createWikiset();
      wikiset = wikisetDao.getWikiset();
    }
    return wikiset;
  }

  public void digestWikiset() {
    Wikiset wikiset = findOrCreateWikiset();
    // - get the current importProgress() possibly by checking for the last edge or
    // node imported with the maximum value for the srcRecordLine, srcRecordFile
    // duo()
    // - this should at the very least be item.csv() line 1
    // essentially the item.csv is the entry point for one prong of the import
    // process
    // - the concurrent process could be unpacking the incredible amount of
    // statements.csv() lines into links - the problem here being that potentially
    // if this process happening concurrently
  }

}
