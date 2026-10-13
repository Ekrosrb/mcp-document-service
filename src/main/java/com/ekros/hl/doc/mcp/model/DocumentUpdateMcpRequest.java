package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ekros.hl.doc.repository.model.*;
import com.ekros.hl.doc.validation.DocumentUpdateValid;
import com.ekros.hl.doc.validation.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@DocumentUpdateValid
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateMcpRequest {
  @JsonPropertyDescription(McpDescriptions.TYPE)
  @JsonProperty(required = false)
  private DocumentType type;
  @JsonPropertyDescription(McpDescriptions.NAME)
  @JsonProperty(required = false)
  @Size(max = 250)
  @NullOrNotBlank
  private String name;
  @JsonPropertyDescription(McpDescriptions.PROJECT_KEY)
  @JsonProperty(required = false)
  @Size(max = 250)
  @NullOrNotBlank
  private String projectKey;
  @JsonPropertyDescription(McpDescriptions.SUMMARY)
  @JsonProperty(required = false)
  @Size(max = 1024)
  @NullOrNotBlank
  private String summary;
  @JsonPropertyDescription(McpDescriptions.CONTENT)
  @JsonProperty(required = false)
  @NullOrNotBlank
  private String content;
  @JsonPropertyDescription(McpDescriptions.CUSTOM_EXTENSION)
  @JsonProperty(required = false)
  @Size(max = 50)
  @NullOrNotBlank
  private String customExtension;
}
