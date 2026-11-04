package io.github.ekrosrb.doc.mcp.model;

import io.github.ekrosrb.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.ekrosrb.doc.repository.model.AuthorType;
import io.github.ekrosrb.doc.repository.model.DocumentScope;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchMcpRequest {
  @JsonPropertyDescription(McpDescriptions.SEARCH_SCOPE)
  @JsonProperty(required = false)
  private DocumentScope scope;
  @JsonPropertyDescription(McpDescriptions.SEARCH_AUTHOR)
  @JsonProperty(required = false)
  private AuthorType author;
  @JsonPropertyDescription(McpDescriptions.SEARCH_PROJECT_KEY)
  @JsonProperty(required = false)
  private String projectKey;
  @JsonPropertyDescription(McpDescriptions.SEARCH_AUTHOR_NAME)
  @JsonProperty(required = false)
  private String authorName;
  @JsonPropertyDescription(McpDescriptions.SEARCH_QUERY)
  @JsonProperty(required = false)
  private String query;
  @JsonPropertyDescription(McpDescriptions.INCLUDE_CONTENT)
  @JsonProperty(required = false)
  private boolean includeContent = false;
  @JsonPropertyDescription(McpDescriptions.INCLUDE_SUMMARY)
  @JsonProperty(required = false)
  private boolean includeSummary = false;
  @JsonPropertyDescription(McpDescriptions.INCLUDE_RELATIONS)
  @JsonProperty(required = false)
  private boolean includeRelations = false;
  @JsonPropertyDescription(McpDescriptions.INCLUDE_SUPERSEDED)
  @JsonProperty(required = false)
  private boolean includeSuperseded = false;
}
