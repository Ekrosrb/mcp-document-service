package com.ekros.hl.doc.service.impl;

import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.controller.model.DocumentSearchRequest;
import com.ekros.hl.doc.exception.DocumentNotFoundException;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.repository.DocumentRepository;
import com.ekros.hl.doc.repository.ProjectRepository;
import com.ekros.hl.doc.repository.model.ProjectData;
import com.ekros.hl.doc.repository.specification.DocumentSearchSpecifications;
import com.ekros.hl.doc.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final ProjectRepository projectRepository;
  private final DocumentMapper documentMapper;

  @Override
  @Transactional
  public DocumentCreateResponse createDocument(DocumentCreateRequest request) {
    log.info("Creating new document with kind [{}], scope [{}] and author [{}]", request.getKind(), request.getScope(), request.getAuthor());

    updateProjectIfPresent(request.getProjectKey());

    var content = documentMapper.mapToDocumentContentData(request);

    var documentData = documentMapper.mapToDocumentData(request, content);
    documentData = documentRepository.save(documentData);
    log.info("Document saved with id [{}]", documentData.getId());
    return documentMapper.mapToDocumentCreateResponse(documentData);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentResponse getDocument(String id, boolean includeContent) {
    return documentRepository.findById(UUID.fromString(id))
        .map(doc -> documentMapper.mapToDocumentResponse(doc, includeContent))
        .orElseThrow(() -> new DocumentNotFoundException(id));
  }

  @Override
  public void removeDocument(String id) {
    log.info("Removing document with id [{}]", id);
    documentRepository.deleteById(UUID.fromString(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<DocumentResponse> search(DocumentSearchRequest request) {
    var spec = DocumentSearchSpecifications.withFilters(request);
    return documentRepository.findAll(spec)
        .stream()
        .map(documentData -> documentMapper.mapToDocumentResponseLight(documentData, request.isIncludeContent(), request.isIncludeSummary()))
        .toList();
  }

  private void updateProjectIfPresent(String projectKey) {
    if (StringUtils.isBlank(projectKey) || projectRepository.existsByProjectKey(projectKey)) {
      return;
    }

    var project = ProjectData.builder().projectKey(projectKey).build();
    project = projectRepository.save(project);
    log.info("New project [{}] is created with id [{}]", project.getProjectKey(), project.getId());
  }

}
