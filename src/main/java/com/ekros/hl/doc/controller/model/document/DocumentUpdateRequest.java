package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.*;
import com.ekros.hl.doc.validation.DocumentUpdateValid;
import com.ekros.hl.doc.validation.NullOrNotBlank;
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
public class DocumentUpdateRequest {
  private DocumentType type;
  private MemoryType memoryType;
  @Size(max = 250)
  @NullOrNotBlank
  private String name;
  @Size(max = 250)
  @NullOrNotBlank
  private String authorName;
  @Size(max = 250)
  @NullOrNotBlank
  private String projectKey;
  @Size(max = 1024)
  @NullOrNotBlank
  private String summary;
  @NullOrNotBlank
  private String content;
  @Size(max = 50)
  @NullOrNotBlank
  private String customExtension;
  private Boolean allowMcpWrite;
}
