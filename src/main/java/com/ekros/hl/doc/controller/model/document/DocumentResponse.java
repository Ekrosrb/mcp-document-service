package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import com.ekros.hl.doc.repository.model.MemoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

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
  private MemoryType memoryType;
  private String name;
  private String authorName;
  private String projectKey;
  private List<DocumentRelation> relations;
  private List<DocumentRelation> referencedBy;
  private String summary;
  private String content;
  private String customExtension;
  private Boolean allowMcpWrite;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
