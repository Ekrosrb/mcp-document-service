package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.validation.DocumentSearchValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DocumentSearchValid
public class DocumentSearchMcpRequest {
  @JsonProperty(required = false)
  private DocumentKind kind;
  @JsonProperty(required = false)
  private DocumentScope scope;
  @JsonProperty(required = false)
  private AuthorType author;
  @JsonProperty(required = false)
  private String projectKey;
  @JsonProperty(required = false)
  private String authorName;
  @JsonProperty(required = false)
  private String query;
  @JsonProperty(required = false)
  private boolean includeContent = false;
  @JsonProperty(required = false)
  private boolean includeSummary = false;
  @JsonProperty(required = false)
  private boolean includeRelations = false;
}
