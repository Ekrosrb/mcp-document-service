package com.ekros.hl.doc.it.support;

import com.ekros.hl.doc.it.testutil.ItTestData;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * State of one scenario: its unique values, the feature it belongs to and the last response.
 */
public class ScenarioContext {

  private final Map<String, String> values = new ConcurrentHashMap<>();
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
   * Returns the value behind a placeholder name, generating a unique one on first use.
   */
  public String value(String name) {
    return values.computeIfAbsent(name, key -> ItTestData.buildUniqueValue());
  }

  public void remember(String name, String value) {
    values.put(name, value);
  }

  public Collection<String> values() {
    return List.copyOf(values.values());
  }

  /**
   * Replaces every {{name}} placeholder with this scenario's value for that name.
   */
  public String resolve(String text) {
    return ItTestData.PLACEHOLDER.matcher(text).replaceAll(match -> value(match.group(1)));
  }

  /**
   * Reads a fixture stored beside the feature file and resolves its placeholders.
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
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
      if (stream == null) {
        throw new IOException("Fixture not found: [%s]".formatted(resource));
      }
      return stream.readAllBytes();
    }
  }
}
