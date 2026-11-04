package io.github.ekrosrb.doc.controller.api;

import io.github.ekrosrb.doc.controller.model.project.ProjectsInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

public interface ProjectApi {

  Page<ProjectsInfoResponse> getProjectsInfo(@PageableDefault(size = 100) Pageable pageable);

}
