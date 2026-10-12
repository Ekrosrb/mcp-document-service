package com.ekros.hl.doc.it.testutil;

import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ItTestData {

  public static final String IT_PROFILE = "it";

  public static final String SERVER_PORT_PROPERTY = "server.port";
  public static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";
  public static final String DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
  public static final String DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";

  public static final String MCP_ENDPOINT_PROPERTY = "spring.ai.mcp.server.streamable-http.mcp-endpoint";
  public static final String MCP_AGENT_NAME_PLACEHOLDER = "agentName";
  public static final String MCP_CLIENT_VERSION = "0.0.1";

  public static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String JSON_CONTENT_TYPE = "application/json";

  public static final String CLASSPATH_SCHEME = "classpath";
  public static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)}}");
  public static final Pattern SQL_IDENTIFIER = Pattern.compile("[a-z_][a-z0-9_]*");

  public static final List<String> CLEANUP_TABLES = List.of("document");

  private static final String URL = "http://localhost:%s%s";
  private static final String INSERT_ROWS_SQL =
      "insert into %1$s select * from json_populate_recordset(null::%1$s, ?::json)";
  private static final String SELECT_ROWS_SQL =
      "select coalesce(json_agg(to_jsonb(t)), '[]')::text from %s t where t.%s::text = ?";
  private static final String DELETE_SCENARIO_ROWS_SQL =
      "delete from %s t where exists (select 1 from jsonb_each_text(to_jsonb(t)) field where field.value = any(?))";

  private ItTestData() {
  }

  public static String buildUniqueValue() {
    return UUID.randomUUID().toString();
  }

  public static String buildUrl(String port, String path) {
    return URL.formatted(port, path);
  }

  public static McpSchema.Implementation buildMcpClientInfo(String agentName) {
    return new McpSchema.Implementation(agentName, MCP_CLIENT_VERSION);
  }

  public static String buildInsertRowsSql(String table) {
    return INSERT_ROWS_SQL.formatted(buildIdentifier(table));
  }

  public static String buildSelectRowsSql(String table, String column) {
    return SELECT_ROWS_SQL.formatted(buildIdentifier(table), buildIdentifier(column));
  }

  public static String buildDeleteScenarioRowsSql(String table) {
    return DELETE_SCENARIO_ROWS_SQL.formatted(buildIdentifier(table));
  }

  private static String buildIdentifier(String name) {
    if (!SQL_IDENTIFIER.matcher(name).matches()) {
      throw new IllegalArgumentException("Not a plain SQL identifier: [%s]".formatted(name));
    }
    return name;
  }
}
