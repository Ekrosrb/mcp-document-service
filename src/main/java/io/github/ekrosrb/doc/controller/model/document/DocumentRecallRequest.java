package io.github.ekrosrb.doc.controller.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task to gather context for: the entries matching its words and the entries linked to them.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRecallRequest {
  public static final int DEFAULT_LIMIT = 10;
  public static final int MAX_LIMIT = 50;
  public static final int DEFAULT_DEPTH = 1;
  public static final int MAX_DEPTH = 2;
  public static final double MIN_RELATIVE_RANK = 0.3;

  private String task;
  private String projectKey;
  private int limit;
  private int depth;
  private boolean includeContent;
}
