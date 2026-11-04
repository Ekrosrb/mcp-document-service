package io.github.ekrosrb.doc.exception;

import lombok.Getter;

@Getter
public class DocumentWriteForbiddenException extends RuntimeException {

  private static final String ERROR_MESSAGE = "You are not allowed to modify document: [%s]";

  private final Long id;

  public DocumentWriteForbiddenException(Long id) {
    super(String.format(ERROR_MESSAGE, id));
    this.id = id;
  }
}
