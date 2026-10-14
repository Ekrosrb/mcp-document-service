package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.mcp.McpDescriptions;
import com.ekros.hl.doc.validation.DocumentRelationValid;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
public class DocumentRelationRemoveRequest {
  @JsonPropertyDescription(McpDescriptions.RELATION_DOCUMENT_ID)
  @Positive
  private long documentId;
  @JsonPropertyDescription(McpDescriptions.RELATION_RELATED_DOCUMENT_ID)
  @Positive
  private long relatedDocumentId;
}
