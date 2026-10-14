package com.ekros.hl.doc.it.support;

import com.ekros.hl.doc.it.testutil.ItTestData;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * State of one scenario: its runtime variables, the feature it belongs to and the last response.
 */
public class ScenarioContext {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<LinkedHashMap<String, String>> DECLARED_VARIABLES_TYPE = new TypeReference<>() {
  };

  private final Map<String, Variable> variables = new ConcurrentHashMap<>();
  private URI featureUri;
  private HttpResponse<String> response;

  public void setFeatureUri(URI featureUri) {
    this.featureUri = featureUri;
  }

  public HttpResponse<String> getResponse() {
    return response;
  }

  public void setResponse(HttpResponse<String> response) {
    this.response = response;
  }

  /**
   * Generates a unique value for every variable declared in a params file stored beside the feature.
   */
  public void generateVariables(String paramsFile) {
    try {
      var declared = OBJECT_MAPPER.readValue(readFixture(paramsFile), DECLARED_VARIABLES_TYPE);
      declared.forEach((token, typeName) -> {
        var type = VariableType.valueOf(typeName.toUpperCase());
        variables.put(ItTestData.buildVariableName(token), new Variable(ItTestData.buildUniqueValue(type), type, true));
      });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns the value of a declared or remembered variable.
   */
  public String value(String name) {
    var variable = variables.get(name);
    if (variable == null) {
      throw new IllegalStateException("Unknown variable: [%s]".formatted(name));
    }
    return variable.value();
  }

  /**
   * Stores a value produced by the service as a variable, typed after its JSON type.
   */
  public void remember(String name, JsonNode value) {
    var type = value.isNumber() ? VariableType.LONG : VariableType.STRING;
    variables.put(name, new Variable(value.asString(), type, false));
  }

  /**
   * Stores a variable set by a scenario step, typed as long when the value is a whole number.
   */
  public void define(String name, String value) {
    var type = ItTestData.LONG_VALUE.matcher(value).matches() ? VariableType.LONG : VariableType.STRING;
    variables.put(name, new Variable(value, type, false));
  }

  /**
   * Returns the values generated for this scenario. Values produced by the service are left out,
   * because a database-assigned id is not unique across tables.
   */
  public Collection<String> generatedValues() {
    return variables.values().stream()
        .filter(Variable::generated)
        .map(Variable::value)
        .toList();
  }

  /**
   * Replaces every variable token with this scenario's value. A quoted token of a long variable
   * becomes a bare number, so fixtures stay valid JSON before and after the replacement.
   */
  public String resolve(String text) {
    var resolved = text;
    for (var variable : variables.entrySet()) {
      var value = variable.getValue().value();
      if (variable.getValue().type() == VariableType.LONG) {
        resolved = resolved.replace(ItTestData.buildQuotedVariableToken(variable.getKey()), value);
      }
      resolved = resolved.replace(ItTestData.buildVariableToken(variable.getKey()), value);
    }
    var unknown = ItTestData.VARIABLE_TOKEN.matcher(resolved);
    if (unknown.find()) {
      throw new IllegalStateException("Unknown variable: [%s]".formatted(unknown.group(1)));
    }
    return resolved;
  }

  /**
   * Reads a fixture stored beside the feature file and resolves its variables.
   */
  public String fixture(String relativePath) {
    try {
      return resolve(new String(readFixture(relativePath), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private byte[] readFixture(String relativePath) throws IOException {
    if (!ItTestData.CLASSPATH_SCHEME.equals(featureUri.getScheme())) {
      return Files.readAllBytes(Path.of(featureUri).resolveSibling(relativePath));
    }
    var resource = Path.of(featureUri.getSchemeSpecificPart()).resolveSibling(relativePath)
        .toString().replace('\\', '/');
    return readResource(resource);
  }

  private static byte[] readResource(String resource) throws IOException {
    try (InputStream stream = ScenarioContext.class.getClassLoader().getResourceAsStream(resource)) {
      if (stream == null) {
        throw new IOException("Fixture not found: [%s]".formatted(resource));
      }
      return stream.readAllBytes();
    }
  }

  private record Variable(String value, VariableType type, boolean generated) {
  }
}
