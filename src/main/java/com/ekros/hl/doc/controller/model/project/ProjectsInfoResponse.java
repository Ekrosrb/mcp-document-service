package com.ekros.hl.doc.controller.model.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsInfoResponse {
  private String projectKey;
  private long documents;
}
