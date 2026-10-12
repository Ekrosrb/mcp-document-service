package com.ekros.hl.doc.controller.model;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchRequest {
  private DocumentKind kind;
  private DocumentScope scope;
  private AuthorType author;
  private String projectKey;
  private String authorName;
  private boolean includeContent;
  private boolean includeSummary;
}
