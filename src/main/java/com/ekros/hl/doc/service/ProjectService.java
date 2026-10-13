package com.ekros.hl.doc.service;

import com.ekros.hl.doc.controller.model.project.ProjectsInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
  Page<ProjectsInfoResponse> getProjectsInfo(Pageable pageable);
}
