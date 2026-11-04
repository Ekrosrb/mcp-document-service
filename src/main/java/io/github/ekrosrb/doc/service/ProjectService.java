package io.github.ekrosrb.doc.service;

import io.github.ekrosrb.doc.controller.model.project.ProjectsInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
  Page<ProjectsInfoResponse> getProjectsInfo(Pageable pageable);
}
