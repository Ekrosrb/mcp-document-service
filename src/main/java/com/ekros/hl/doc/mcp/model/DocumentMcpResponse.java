package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.controller.model.document.DocumentRelation;
import com.ekros.hl.doc.repository.model.*;
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
public class DocumentMcpResponse {
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
  private Boolean allowWrite;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
