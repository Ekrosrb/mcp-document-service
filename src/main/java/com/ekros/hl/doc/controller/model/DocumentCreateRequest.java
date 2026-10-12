package com.ekros.hl.doc.controller.model;

import com.ekros.hl.doc.repository.model.AuthorType;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.validation.DocumentCreateValid;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  @NotNull
  private AuthorType author;
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
}
