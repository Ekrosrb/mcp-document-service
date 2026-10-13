package com.ekros.hl.doc.service.impl;

import com.ekros.hl.doc.controller.model.project.ProjectsInfoResponse;
import com.ekros.hl.doc.mapper.ProjectMapper;
import com.ekros.hl.doc.repository.DocumentRepository;
import com.ekros.hl.doc.repository.ProjectRepository;
import com.ekros.hl.doc.repository.model.ProjectData;
import com.ekros.hl.doc.repository.projection.ProjectDocumentsCountProjection;
import com.ekros.hl.doc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final DocumentRepository documentRepository;
  private final ProjectMapper projectMapper;

  @Override
  public Page<ProjectsInfoResponse> getProjectsInfo(Pageable pageable) {
    log.info("Getting projects info, page [{}], size [{}]", pageable.getPageNumber(), pageable.getPageSize());
    var projects = projectRepository.findAll(pageable).map(ProjectData::getProjectKey);

    var projectsDocumentCount = documentRepository.countBySpecificProjectKeys(projects.getContent())
        .stream()
        .collect(Collectors.toMap(
            ProjectDocumentsCountProjection::getProjectKey,
            ProjectDocumentsCountProjection::getCount
        ));

    return projects.map(projectKey ->
        projectMapper.mapToProjectInfoResponse(projectKey, projectsDocumentCount.getOrDefault(projectKey, 0L)));
  }
}
