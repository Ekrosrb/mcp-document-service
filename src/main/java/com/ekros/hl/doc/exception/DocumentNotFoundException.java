package com.ekros.hl.doc.exception;

import lombok.Getter;

@Getter
public class DocumentNotFoundException extends RuntimeException {

  private static final String ERROR_MESSAGE = "Document with id [%s] not found";
  private final String id;

  public DocumentNotFoundException(String id) {
    super(String.format(ERROR_MESSAGE, id));
    this.id = id;
  }
}
