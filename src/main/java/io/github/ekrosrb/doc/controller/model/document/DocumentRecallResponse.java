package io.github.ekrosrb.doc.controller.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entries matching a task, best match first, and the entries linked to them, nearest first.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRecallResponse {
  private List<DocumentResponse> matches;
  private List<DocumentResponse> linked;
}
