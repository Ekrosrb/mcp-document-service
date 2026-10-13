package com.ekros.hl.doc.it.steps;

import com.ekros.hl.doc.it.support.ApplicationHooks;
import com.ekros.hl.doc.it.support.ScenarioContext;
import com.ekros.hl.doc.it.testutil.ItTestData;
import dev.toonformat.jtoon.JToon;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import net.javacrumbs.jsonunit.core.Option;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;

public class McpSteps {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<Map<String, Object>> ARGUMENTS_TYPE = new TypeReference<>() {
  };

  private final ScenarioContext context;
  private McpSyncClient client;
  private CallToolResult result;

  public McpSteps(ScenarioContext context) {
    this.context = context;
  }

  @When("I call the MCP tool {string} with arguments {string}")
  public void callTool(String tool, String argumentsFixture) {
    var arguments = OBJECT_MAPPER.readValue(context.fixture(argumentsFixture), ARGUMENTS_TYPE);
    result = client().callTool(new CallToolRequest(tool, arguments));
  }

  @Then("the MCP tool call succeeds")
  public void assertToolCallSucceeded() {
    assertThat(result.isError()).as("tool result: %s", resultText()).isNotEqualTo(Boolean.TRUE);
  }

  @Then("the MCP tool call fails")
  public void assertToolCallFailed() {
    assertThat(result.isError()).as("tool result: %s", resultText()).isTrue();
  }

  @Then("the MCP tool result decoded from TOON matches {string}")
  public void assertToolResult(String expectedFixture) {
    assertThatJson(JToon.decodeToJson(resultText()))
        .when(Option.IGNORING_ARRAY_ORDER, Option.TREATING_NULL_AS_ABSENT)
        .isEqualTo(context.fixture(expectedFixture));
  }

  @Then("the MCP tool result decoded from TOON contains the element from {string}")
  public void assertToolResultContainsElement(String expectedFixture) {
    assertThatJson(JToon.decodeToJson(resultText()))
        .isArray()
        .contains(json(context.fixture(expectedFixture)));
  }

  @Then("the MCP tool result is the text from {string}")
  public void assertToolResultText(String expectedFixture) {
    assertThat(resultText()).isEqualToNormalizingNewlines(context.fixture(expectedFixture).strip());
  }

  @Then("I remember the MCP tool result field {string} as {string}")
  public void rememberToolResultField(String field, String name) {
    var value = OBJECT_MAPPER.readTree(JToon.decodeToJson(resultText())).path(field);
    assertThat(value.isString()).as("tool result field [%s]", field).isTrue();
    context.remember(name, value.asString());
  }

  @After
  public void closeClient() {
    if (client != null) {
      client.close();
    }
  }

  private McpSyncClient client() {
    if (client == null) {
      var transport = HttpClientStreamableHttpTransport
          .builder(ItTestData.buildUrl(ApplicationHooks.property(ItTestData.SERVER_PORT_PROPERTY), ""))
          .endpoint(ApplicationHooks.property(ItTestData.MCP_ENDPOINT_PROPERTY))
          .build();
      client = McpClient.sync(transport)
          .clientInfo(ItTestData.buildMcpClientInfo(context.value(ItTestData.MCP_AGENT_NAME_PLACEHOLDER)))
          .build();
      client.initialize();
    }
    return client;
  }

  private String resultText() {
    return result.content().stream()
        .filter(TextContent.class::isInstance)
        .map(content -> ((TextContent) content).text())
        .collect(Collectors.joining());
  }
}
