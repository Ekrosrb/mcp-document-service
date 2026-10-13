package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
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
public class DocumentCreateMcpRequest {
  @NotNull
  private DocumentScope scope;
  @JsonProperty(required = false)
  private DocumentType type;
  @Size(max = 250)
  @JsonProperty(required = false)
  private String name;
  @Size(max = 250)
  @JsonProperty(required = false)
  private String projectKey;
  @NotBlank
  @Size(max = 1024)
  private String summary;
  @NotBlank
  private String content;
  @JsonProperty(required = false)
  private String customExtension;
}
