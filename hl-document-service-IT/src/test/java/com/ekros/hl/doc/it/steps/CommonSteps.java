package com.ekros.hl.doc.it.steps;

import com.ekros.hl.doc.it.support.ApplicationHooks;
import com.ekros.hl.doc.it.support.ScenarioContext;
import com.ekros.hl.doc.it.testutil.ItTestData;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.javacrumbs.jsonunit.core.Option;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final ScenarioContext context;

  public CommonSteps(ScenarioContext context) {
    this.context = context;
  }

  @Before
  public void prepareScenario(Scenario scenario) {
    context.setFeatureUri(scenario.getUri());
    context.generateVariables(ItTestData.buildParamsFile(scenario.getSourceTagNames()));
  }

  @Given("the variable {string} is {string}")
  public void defineVariable(String name, String value) {
    context.define(name, context.resolve(value));
  }

  @When("I send a {word} request to {string}")
  public void sendRequest(String method, String path) throws Exception {
    send(method, path, BodyPublishers.noBody());
  }

  @When("I send a {word} request to {string} with body {string}")
  public void sendRequestWithBody(String method, String path, String bodyFixture) throws Exception {
    send(method, path, BodyPublishers.ofString(context.fixture(bodyFixture)));
  }

  @Then("the response status is {int}")
  public void assertResponseStatus(int status) {
    assertThat(context.getResponse().statusCode())
        .as("response body: %s", context.getResponse().body())
        .isEqualTo(status);
  }

  @Then("the response body matches {string}")
  public void assertResponseBody(String expectedFixture) {
    assertThatJson(context.getResponse().body())
        .when(Option.IGNORING_ARRAY_ORDER, Option.TREATING_NULL_AS_ABSENT)
        .isEqualTo(context.fixture(expectedFixture));
  }

  @Then("the response body at {string} contains the element from {string}")
  public void assertResponseBodyContainsElement(String path, String expectedFixture) {
    assertThatJson(context.getResponse().body())
        .inPath(path)
        .isArray()
        .contains(json(context.fixture(expectedFixture)));
  }

  @Then("the response body is empty")
  public void assertResponseBodyEmpty() {
    assertThat(context.getResponse().body()).isEmpty();
  }

  @Then("I remember the response field {string} as {string}")
  public void rememberResponseField(String field, String name) {
    var value = OBJECT_MAPPER.readTree(context.getResponse().body()).path(field);
    assertThat(value.isString() || value.isNumber()).as("response field [%s]", field).isTrue();
    context.remember(name, value);
  }

  private void send(String method, String path, BodyPublisher body) throws Exception {
    var port = ApplicationHooks.property(ItTestData.SERVER_PORT_PROPERTY);
    var request = HttpRequest.newBuilder(URI.create(ItTestData.buildUrl(port, context.resolve(path))))
        .header(ItTestData.CONTENT_TYPE_HEADER, ItTestData.JSON_CONTENT_TYPE)
        .method(method, body)
        .build();
    context.setResponse(HTTP_CLIENT.send(request, BodyHandlers.ofString()));
  }
}
