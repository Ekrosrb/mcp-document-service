package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.mcp.McpDescriptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ekros.hl.doc.repository.model.DocumentRelationType;
import com.ekros.hl.doc.validation.DocumentRelationValid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DocumentRelationValid
public class DocumentRelationCreateRequest {
  @JsonPropertyDescription(McpDescriptions.RELATION_DOCUMENT_ID)
  @Positive
  private long documentId;
  @JsonPropertyDescription(McpDescriptions.RELATION_RELATED_DOCUMENT_ID)
  @Positive
  private long relatedDocumentId;
  @JsonPropertyDescription(McpDescriptions.RELATION_TYPE)
  @NotNull
  private DocumentRelationType type;
}
