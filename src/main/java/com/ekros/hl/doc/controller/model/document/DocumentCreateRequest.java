package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.*;
import com.ekros.hl.doc.validation.DocumentCreateValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
  @Valid
  private List<DocumentRelation> relations;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DocumentRelation {
    @NotNull
    @Positive
    private Long relatedDocumentId;
    @NotNull
    private DocumentRelationType type;
  }
}
