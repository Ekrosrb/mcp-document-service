package com.ekros.hl.doc.repository.model;

public enum DocumentType {
  TEXT(".txt"),
  JSON(".json"),
  HLD(".hld"),
  XML(".xml"),
  MARKDOWN(".md"),
  TOON(".toon"),
  CUSTOM("");

  private final String extension;

  DocumentType(String extension) {
    this.extension = extension;
  }

  public String extension() {
    return extension;
  }
}
