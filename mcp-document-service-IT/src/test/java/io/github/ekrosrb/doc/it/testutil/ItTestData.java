package io.github.ekrosrb.doc.it.testutil;

import io.github.ekrosrb.doc.it.support.VariableType;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public final class ItTestData {

  public static final String IT_PROFILE = "it";

  public static final String SERVER_PORT_PROPERTY = "server.port";
  public static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";
  public static final String DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
  public static final String DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";

  public static final String MCP_ENDPOINT_PROPERTY = "spring.ai.mcp.server.streamable-http.mcp-endpoint";
  public static final String MCP_AGENT_NAME_VARIABLE = "agentName";
  public static final String MCP_CLIENT_VERSION = "0.0.1";
  public static final String MCP_TOOL_NAME_FIELD = "name";
  public static final String MCP_TOOL_INPUT_SCHEMA_FIELD = "inputSchema";
  public static final String JSON_SCHEMA_PROPERTIES_FIELD = "properties";
  public static final String JSON_SCHEMA_ITEMS_FIELD = "items";
  public static final String DESCRIPTION_FIELD = "description";

  public static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String JSON_CONTENT_TYPE = "application/json";

  public static final String CLASSPATH_SCHEME = "classpath";
  public static final String DEFAULT_PARAMS_FILE = "params/params.json";
  public static final String PARAMS_TAG_PREFIX = "@params:";
  public static final Pattern LONG_VALUE = Pattern.compile("-?\\d+");
  public static final Pattern VARIABLE_TOKEN = Pattern.compile("\\$\\{(\\w+)}");
  public static final Pattern SQL_IDENTIFIER = Pattern.compile("[a-z_][a-z0-9_]*");

  public static final List<String> CLEANUP_TABLES = List.of("document_content", "project");

  public static final long ID_RANGE_PER_MILLISECOND = 1_000L;

  private static final AtomicLong UNIQUE_ID = new AtomicLong(System.currentTimeMillis() * ID_RANGE_PER_MILLISECOND);
  private static final String VARIABLE_TOKEN_FORMAT = "${%s}";
  private static final String QUOTED_VARIABLE_TOKEN_FORMAT = "\"${%s}\"";
  private static final String SCHEMA_PATH_FORMAT = "%s.%s";
  private static final String URL = "http://localhost:%s%s";
  private static final String INSERT_ROWS_SQL =
      "insert into %1$s overriding system value select * from json_populate_recordset(null::%1$s, ?::json)";
  private static final String SELECT_ROWS_SQL =
      "select coalesce(json_agg(to_jsonb(t)), '[]')::text from %s t where t.%s::text = ?";
  private static final String DELETE_SCENARIO_ROWS_SQL =
      "delete from %s t where exists (select 1 from jsonb_each_text(to_jsonb(t)) field where field.value = any(?))";
  private static final String DELETE_SCENARIO_DOCUMENTS_SQL =
      DELETE_SCENARIO_ROWS_SQL.formatted("document") + " returning content_id::text";
  private static final String DELETE_DOCUMENT_CONTENTS_SQL = "delete from document_content where id::text = any(?)";

  private ItTestData() {
  }

  public static String buildUniqueValue(VariableType type) {
    return switch (type) {
      case LONG -> String.valueOf(UNIQUE_ID.incrementAndGet());
      case STRING -> UUID.randomUUID().toString();
    };
  }

  /**
   * Returns the params file of a scenario: the one named by its last "@params:" tag, or the feature default.
   */
  public static String buildParamsFile(Collection<String> tags) {
    return tags.stream()
        .filter(tag -> tag.startsWith(PARAMS_TAG_PREFIX))
        .map(tag -> tag.substring(PARAMS_TAG_PREFIX.length()))
        .reduce((first, second) -> second)
        .orElse(DEFAULT_PARAMS_FILE);
  }

  public static String buildVariableToken(String name) {
    return VARIABLE_TOKEN_FORMAT.formatted(name);
  }

  public static String buildQuotedVariableToken(String name) {
    return QUOTED_VARIABLE_TOKEN_FORMAT.formatted(name);
  }

  public static String buildVariableName(String token) {
    var matcher = VARIABLE_TOKEN.matcher(token);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Not a variable token: [%s]".formatted(token));
    }
    return matcher.group(1);
  }

  public static String buildSchemaPath(String parent, String property) {
    return SCHEMA_PATH_FORMAT.formatted(parent, property);
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

  public static String buildDeleteScenarioDocumentsSql() {
    return DELETE_SCENARIO_DOCUMENTS_SQL;
  }

  public static String buildDeleteDocumentContentsSql() {
    return DELETE_DOCUMENT_CONTENTS_SQL;
  }

  private static String buildIdentifier(String name) {
    if (!SQL_IDENTIFIER.matcher(name).matches()) {
      throw new IllegalArgumentException("Not a plain SQL identifier: [%s]".formatted(name));
    }
    return name;
  }
}
