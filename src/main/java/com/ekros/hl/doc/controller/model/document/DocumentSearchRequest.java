package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.MemoryType;
import com.ekros.hl.doc.validation.DocumentSearchValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DocumentSearchValid
public class DocumentSearchRequest {
  public static final int DEFAULT_PAGE_SIZE = 10;
  public static final int MAX_MCP_PAGE_SIZE = 50;

  private DocumentKind kind;
  private DocumentScope scope;
  private AuthorType author;
  private MemoryType memoryType;
  private String projectKey;
  private String authorName;
  private String query;
  private boolean includeContent;
  private boolean includeSummary;
  private boolean includeRelations;
  private boolean includeSuperseded;
}
