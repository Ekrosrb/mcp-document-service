package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ekros.hl.doc.repository.model.MemoryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MemorySearchMcpRequest extends DocumentSearchMcpRequest {
  @JsonPropertyDescription(McpDescriptions.SEARCH_MEMORY_TYPE)
  @JsonProperty(required = false)
  private MemoryType memoryType;
}
