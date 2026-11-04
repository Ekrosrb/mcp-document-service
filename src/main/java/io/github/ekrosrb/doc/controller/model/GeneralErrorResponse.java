package io.github.ekrosrb.doc.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralErrorResponse {
  private String message;
  private Map<String, Object> metadata;
}
