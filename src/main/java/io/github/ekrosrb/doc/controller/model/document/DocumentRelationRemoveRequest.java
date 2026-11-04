package io.github.ekrosrb.doc.controller.model.document;

import io.github.ekrosrb.doc.mcp.McpDescriptions;
import io.github.ekrosrb.doc.validation.DocumentRelationValid;
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
