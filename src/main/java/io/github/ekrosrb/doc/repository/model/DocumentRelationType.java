package io.github.ekrosrb.doc.repository.model;

/**
 * Meaning of a relation, read from the source document to the related document.
 */
public enum DocumentRelationType {
  DEPENDS_ON,
  SUPERSEDES,
  EXTENDS,
  PART_OF,
  DERIVED_FROM,
  RELATED_TO
}
