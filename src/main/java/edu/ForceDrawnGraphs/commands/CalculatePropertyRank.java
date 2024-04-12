package edu.ForceDrawnGraphs.commands;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.AddBatchToStmt;
import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.functions.ReadSQLFileAsString;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.Property;

@ShellComponent
public class CalculatePropertyRank implements ReadSQLFileAsString, GetPreparedStmt, AddBatchToStmt {
  DataSource dataSource;
  private JdbcTemplate jdbcTemplate;

  public CalculatePropertyRank(DataSource dataSource) {
    this.dataSource = dataSource;
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @ShellMethod("Count the number of references for each property and update the number of references for each property in the database.")
  public void rankProperties() {
    ProcessTimer timer = new ProcessTimer("rankProperties()");
    PreparedStatement preparedStatement = getPreparedStmt("UPDATE properties SET number_of_references = ? WHERE id = ?",
        dataSource);
    List<Property> properties = getPropertiesOrderedByNumberOfStatementReferences();

    for (Property property : properties) {
      updateNumberOfReferences(property, property.getNumberOfReferences(), preparedStatement);
    }
    try {
      preparedStatement.executeBatch();
    } catch (Exception e) {
      report("@CalculatePropertyRank.java - rankProperties():", e);
    } finally {
      timer.end();
    }
  }

  private List<Property> getPropertiesOrderedByNumberOfStatementReferences() {
    ProcessTimer timer = new ProcessTimer("getPropertiesOrderedByNumberOfStatementReferences()");
    String sql = readSQLFileAsString("queries/GetPropsOrderedByNumOfStmtRefs.sql");
    List<Property> properties = new ArrayList<>();

    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      while (results.next()) {
        properties.add(new Property(results.getInt("id"), results.getString("property_id"),
            results.getString("en_label"), results.getString("en_description"), results.getInt("reference_count")));
      }
    } catch (DataAccessException e) {
      report("@CalculatePropertyRank.java - getPropertiesOrderedByNumberOfStatementRefs():", e);
    }
    timer.end();
    return properties;
  }

  public void updateNumberOfReferences(Property property, int newNumberOfReferences,
      PreparedStatement preparedStatement) {
    ProcessTimer timer = new ProcessTimer("updateNumberOfReferences()");
    try {
      preparedStatement.setInt(1, newNumberOfReferences);
      preparedStatement.setInt(2, property.getId());
      addBatchToStmt(preparedStatement);
    } catch (Exception e) {
      report("@CalculatePropertyRank.java - updateNumberOfReferences():", e);
    }
    timer.end();
  }

}
