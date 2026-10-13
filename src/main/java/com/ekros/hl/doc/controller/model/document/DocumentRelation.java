package com.ekros.hl.doc.controller.model.document;

import com.ekros.hl.doc.repository.model.DocumentRelationType;
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
