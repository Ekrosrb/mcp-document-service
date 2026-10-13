package com.ekros.hl.doc.exception;

import lombok.Getter;

@Getter
public class DocumentRelationExistsException extends RuntimeException {

  private static final String ERROR_MESSAGE = "Document relation with document id [%s] and related document id [%s] already exists";
  private final long documentId;
  private final long relatedDocumentId;

  public DocumentRelationExistsException(long documentId, long relatedDocumentId) {
    super(String.format(ERROR_MESSAGE, documentId, relatedDocumentId));
    this.documentId = documentId;
    this.relatedDocumentId = relatedDocumentId;
  }
}
