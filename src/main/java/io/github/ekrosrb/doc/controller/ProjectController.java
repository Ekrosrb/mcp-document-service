package io.github.ekrosrb.doc.controller;

import io.github.ekrosrb.doc.controller.api.ProjectApi;
import io.github.ekrosrb.doc.controller.model.project.ProjectsInfoResponse;
import io.github.ekrosrb.doc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController implements ProjectApi {

  private final ProjectService projectService;

  @Override
  @GetMapping("/info")
  public Page<ProjectsInfoResponse> getProjectsInfo(@PageableDefault(size = 100) Pageable pageable) {
    return projectService.getProjectsInfo(pageable);
  }
}
