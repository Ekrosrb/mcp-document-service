package io.github.ekrosrb.doc.repository.projection;

public interface ProjectDocumentsCountProjection {
    String getProjectKey();
    long getCount();
}