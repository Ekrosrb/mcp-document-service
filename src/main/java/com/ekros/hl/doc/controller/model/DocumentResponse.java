package com.ekros.hl.doc.controller.model;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
  private Long id;
  private DocumentKind kind;
  private DocumentScope scope;
  private DocumentType type;
  private AuthorType author;
  private String name;
  private String authorName;
  private String projectKey;
  private String summary;
  private String content;
  private String customExtension;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
