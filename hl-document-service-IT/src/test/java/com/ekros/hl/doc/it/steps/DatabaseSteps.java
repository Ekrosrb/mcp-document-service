package com.ekros.hl.doc.it.steps;

import com.ekros.hl.doc.it.support.ApplicationHooks;
import com.ekros.hl.doc.it.support.ScenarioContext;
import com.ekros.hl.doc.it.testutil.ItTestData;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.javacrumbs.jsonunit.core.Option;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class DatabaseSteps {

  private final ScenarioContext context;

  public DatabaseSteps(ScenarioContext context) {
    this.context = context;
  }

  @Given("the table {string} contains the rows from {string}")
  public void insertRows(String table, String rowsFixture) throws SQLException {
    try (var connection = connect();
         var statement = connection.prepareStatement(ItTestData.buildInsertRowsSql(table))) {
      statement.setString(1, context.fixture(rowsFixture));
      statement.executeUpdate();
    }
  }

  @Then("the rows of table {string} where {string} is {string} match {string}")
  public void assertRows(String table, String column, String value, String expectedFixture) throws SQLException {
    try (var connection = connect();
         var statement = connection.prepareStatement(ItTestData.buildSelectRowsSql(table, column))) {
      statement.setString(1, context.resolve(value));
      try (var rows = statement.executeQuery()) {
        rows.next();
        assertThatJson(rows.getString(1))
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(context.fixture(expectedFixture));
      }
    }
  }

  /**
   * Deletes the rows that carry one of this scenario's unique values, and no other row.
   */
  @After
  public void removeScenarioRows() throws SQLException {
    var values = context.values().toArray(String[]::new);
    if (values.length == 0) {
      return;
    }
    try (var connection = connect()) {
      for (var table : ItTestData.CLEANUP_TABLES) {
        try (var statement = connection.prepareStatement(ItTestData.buildDeleteScenarioRowsSql(table))) {
          statement.setArray(1, connection.createArrayOf("text", values));
          statement.executeUpdate();
        }
      }
    }
  }

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(
        ApplicationHooks.property(ItTestData.DATASOURCE_URL_PROPERTY),
        ApplicationHooks.property(ItTestData.DATASOURCE_USERNAME_PROPERTY),
        ApplicationHooks.property(ItTestData.DATASOURCE_PASSWORD_PROPERTY));
  }
}
