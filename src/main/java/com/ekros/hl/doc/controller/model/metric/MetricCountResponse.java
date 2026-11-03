package com.ekros.hl.doc.controller.model.metric;

import com.ekros.hl.doc.repository.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricCountResponse {
  private DocumentType type;
  private long count;
}
