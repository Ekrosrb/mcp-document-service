package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import com.ekros.hl.doc.validation.DocumentCreateValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@DocumentCreateValid
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateMcpRequest {
  @JsonPropertyDescription(McpDescriptions.SCOPE)
  @NotNull
  private DocumentScope scope;
  @JsonPropertyDescription(McpDescriptions.TYPE)
  @JsonProperty(required = false)
  private DocumentType type;
  @JsonPropertyDescription(McpDescriptions.NAME)
  @Size(max = 250)
  @JsonProperty(required = false)
  private String name;
  @JsonPropertyDescription(McpDescriptions.PROJECT_KEY)
  @Size(max = 250)
  @JsonProperty(required = false)
  private String projectKey;
  @JsonPropertyDescription(McpDescriptions.SUMMARY)
  @NotBlank
  @Size(max = 1024)
  private String summary;
  @JsonPropertyDescription(McpDescriptions.CONTENT)
  @NotBlank
  private String content;
  @JsonPropertyDescription(McpDescriptions.CUSTOM_EXTENSION)
  @JsonProperty(required = false)
  private String customExtension;
}
