package com.ekros.hl.doc.repository.model;

public enum DocumentType {
  TEXT(".txt"),
  JSON(".json"),
  XML(".xml"),
  MARKDOWN(".md"),
  TOON(".toon"),
  HTML(".html"),
  CSS(".css"),
  CUSTOM("");

  private final String extension;

  DocumentType(String extension) {
    this.extension = extension;
  }

  public String extension() {
    return extension;
  }
}
