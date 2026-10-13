package com.ekros.hl.doc.repository.projection;

public interface ProjectDocumentsCountProjection {
    String getProjectKey();
    long getCount();
}