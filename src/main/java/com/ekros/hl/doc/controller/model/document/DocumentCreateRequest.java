package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import com.ekros.hl.doc.repository.model.MemoryType;
import com.ekros.hl.doc.validation.DocumentCreateValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@DocumentCreateValid
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {
  @NotNull
  private DocumentKind kind;
  @NotNull
  private DocumentScope scope;
  private DocumentType type;
  @NotNull
  private AuthorType author;
  private MemoryType memoryType;
  @Size(max = 250)
  private String name;
  @Size(max = 250)
  private String authorName;
  @Size(max = 250)
  private String projectKey;
  @NotBlank
  @Size(max = 1024)
  private String summary;
  @NotBlank
  private String content;
  @Size(max = 50)
  private String customExtension;
  private Boolean allowMcpWrite;
}
