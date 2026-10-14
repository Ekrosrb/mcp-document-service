package com.ekros.hl.doc.exception;

import lombok.Getter;

@Getter
public class DocumentMemoryTypeChangeException extends RuntimeException {
  private static final String ERROR_MESSAGE = "Memory type can't be specified for document [%s] with kind [DOCUMENT]";
  private final Long id;

  public DocumentMemoryTypeChangeException(Long id) {
    super(String.format(ERROR_MESSAGE, id));
    this.id = id;
  }
}
