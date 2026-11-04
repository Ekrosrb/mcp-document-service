package io.github.ekrosrb.doc.controller.model.document;

import io.github.ekrosrb.doc.repository.model.DocumentRelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRelation {
  private Long id;
  private String name;
  private DocumentRelationType type;
}
