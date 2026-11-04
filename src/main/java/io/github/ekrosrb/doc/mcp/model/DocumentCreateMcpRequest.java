package io.github.ekrosrb.doc.mcp.model;

import io.github.ekrosrb.doc.mcp.McpDescriptions;
import io.github.ekrosrb.doc.repository.model.DocumentRelationType;
import io.github.ekrosrb.doc.repository.model.DocumentScope;
import io.github.ekrosrb.doc.repository.model.DocumentType;
import io.github.ekrosrb.doc.validation.DocumentCreateValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
public class DocumentCreateMcpRequest {
  @JsonPropertyDescription(McpDescriptions.SCOPE)
  @NotNull
  private DocumentScope scope;
  @JsonPropertyDescription(McpDescriptions.TYPE)
  @JsonProperty(required = false)
  private DocumentType type;
  @JsonPropertyDescription(McpDescriptions.NAME)
  @Size(max = 250)
  @JsonProperty(required = false)
  private String name;
  @JsonPropertyDescription(McpDescriptions.PROJECT_KEY)
  @Size(max = 250)
  @JsonProperty(required = false)
  private String projectKey;
  @JsonPropertyDescription(McpDescriptions.SUMMARY)
  @NotBlank
  @Size(max = 1024)
  private String summary;
  @JsonPropertyDescription(McpDescriptions.CONTENT)
  @NotBlank
  private String content;
  @JsonPropertyDescription(McpDescriptions.CUSTOM_EXTENSION)
  @JsonProperty(required = false)
  private String customExtension;
  @JsonPropertyDescription(McpDescriptions.CREATE_RELATIONS)
  @JsonProperty(required = false)
  @Valid
  private List<DocumentRelation> relations;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DocumentRelation {
    @JsonPropertyDescription(McpDescriptions.RELATION_RELATED_DOCUMENT_ID)
    @NotNull
    @Positive
    private Long relatedDocumentId;
    @JsonPropertyDescription(McpDescriptions.CREATE_RELATION_TYPE)
    @NotNull
    private DocumentRelationType type;
  }
}
