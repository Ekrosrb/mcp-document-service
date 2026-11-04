package io.github.ekrosrb.doc.repository.projection;

import io.github.ekrosrb.doc.repository.model.DocumentType;

public interface DocumentTypeCountProjection {
    DocumentType getType();
    long getCount();
}
