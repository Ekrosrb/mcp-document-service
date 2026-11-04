package io.github.ekrosrb.doc.mcp.model;

import io.github.ekrosrb.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.ekrosrb.doc.repository.model.MemoryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MemoryCreateMcpRequest extends DocumentCreateMcpRequest {
  @JsonPropertyDescription(McpDescriptions.MEMORY_TYPE)
  @JsonProperty(required = false)
  private MemoryType memoryType;
}
