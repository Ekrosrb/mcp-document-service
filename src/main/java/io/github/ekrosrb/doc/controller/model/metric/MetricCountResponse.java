package io.github.ekrosrb.doc.controller.model.metric;

import io.github.ekrosrb.doc.repository.model.DocumentType;
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
