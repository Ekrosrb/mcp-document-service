package io.github.ekrosrb.doc.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DocumentRelationId implements Serializable {

  @Column(name = "document_id")
  private Long documentId;

  @Column(name = "related_document_id")
  private Long relatedDocumentId;
}
