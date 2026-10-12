package com.ekros.hl.doc.controller.model;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
  private UUID id;
  private DocumentKind kind;
  private DocumentScope scope;
  private AuthorType author;
  private String name;
  private String authorName;
  private String projectKey;
  private String summary;
  private String content;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
