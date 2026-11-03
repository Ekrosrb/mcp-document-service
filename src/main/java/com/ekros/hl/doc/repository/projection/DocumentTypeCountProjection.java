package com.ekros.hl.doc.repository.projection;

import com.ekros.hl.doc.repository.model.DocumentType;

public interface DocumentTypeCountProjection {
    DocumentType getType();
    long getCount();
}
