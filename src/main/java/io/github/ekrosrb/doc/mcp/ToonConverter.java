package io.github.ekrosrb.doc.mcp;

import dev.toonformat.jtoon.JToon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Renders tool results in TOON, the token-saving text format read by agents.
 */
@Component
@RequiredArgsConstructor
public class ToonConverter {

  private final JsonMapper jsonMapper;

  public String toToon(Object value) {
    return JToon.encodeJson(jsonMapper.writeValueAsString(value));
  }
}
