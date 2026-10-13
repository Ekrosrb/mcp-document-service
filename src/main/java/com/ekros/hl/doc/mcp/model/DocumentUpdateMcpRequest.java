package com.ekros.hl.doc.mcp.model;

import com.ekros.hl.doc.repository.model.*;
import com.ekros.hl.doc.validation.DocumentUpdateValid;
import com.ekros.hl.doc.validation.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@DocumentUpdateValid
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateMcpRequest {
  @JsonProperty(required = false)
  private DocumentType type;
  @JsonProperty(required = false)
  @Size(max = 250)
  @NullOrNotBlank
  private String name;
  @JsonProperty(required = false)
  @Size(max = 250)
  @NullOrNotBlank
  private String projectKey;
  @JsonProperty(required = false)
  @Size(max = 1024)
  @NullOrNotBlank
  private String summary;
  @JsonProperty(required = false)
  @NullOrNotBlank
  private String content;
  @JsonProperty(required = false)
  @Size(max = 50)
  @NullOrNotBlank
  private String customExtension;
}
