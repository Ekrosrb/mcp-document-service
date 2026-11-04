package io.github.ekrosrb.doc.mcp.tools;

import io.github.ekrosrb.doc.mcp.McpDescriptions;
import io.github.ekrosrb.doc.mcp.ToonConverter;
import io.github.ekrosrb.doc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class ProjectTools {

  private final ProjectService projectService;
  private final ToonConverter toonConverter;

  @McpTool(name = "getProjectsInfo", description = McpDescriptions.GET_PROJECTS_INFO)
  public String getProjectsInfo(
      @McpToolParam(description = McpDescriptions.PAGE, required = false) Integer page,
      @McpToolParam(description = McpDescriptions.PAGE_SIZE, required = false) Integer size) {
    int pageNum = (page != null) ? page : 0;
    int pageSize = (size != null) ? size : 100;

    var pageable = PageRequest.of(pageNum, pageSize);

    var productPage = projectService.getProjectsInfo(pageable);
    return toonConverter.toToon(productPage.getContent());
  }


}
