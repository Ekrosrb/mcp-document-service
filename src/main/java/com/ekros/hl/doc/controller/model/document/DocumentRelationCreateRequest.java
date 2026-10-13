package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.DocumentRelationType;
import com.ekros.hl.doc.validation.DocumentRelationCreateValid;
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
@DocumentRelationCreateValid
public class DocumentRelationCreateRequest {
  @Positive
  private long documentId;
  @Positive
  private long relatedDocumentId;
  @NotNull
  private DocumentRelationType type;
}
