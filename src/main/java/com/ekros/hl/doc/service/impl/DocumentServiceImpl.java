package com.ekros.hl.doc.service.impl;

import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.exception.DocumentNotFoundException;
import com.ekros.hl.doc.exception.DocumentRelationExistsException;
import com.ekros.hl.doc.exception.DocumentWriteForbiddenException;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.repository.DocumentRelationRepository;
import com.ekros.hl.doc.repository.DocumentRepository;
import com.ekros.hl.doc.repository.ProjectRepository;
import com.ekros.hl.doc.repository.model.DocumentData;
import com.ekros.hl.doc.repository.model.ProjectData;
import com.ekros.hl.doc.repository.specification.DocumentSearchSpecifications;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final DocumentRelationRepository documentRelationRepository;
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
  @Transactional
  public void updateDocument(Long id, DocumentUpdateRequest request, boolean isMcp) {
    log.info("Updating document with id [{}]", id);
    var documentData = documentRepository.findById(id)
        .orElseThrow(() -> new DocumentNotFoundException(id));

    validateAllowMcpWrite(documentData, isMcp);

    documentMapper.updateDocument(documentData, request);
    documentData.setUpdatedAt(Timestamp.from(Instant.now()));

    updateProjectIfPresent(request.getProjectKey());
    documentRepository.save(documentData);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentResponse getDocument(Long id, boolean includeContent) {
    return documentRepository.findById(id)
        .map(doc -> documentMapper.mapToDocumentResponse(doc, includeContent))
        .map(doc -> {
          populateWithReferencedBy(doc);
          return doc;
        })
        .orElseThrow(() -> new DocumentNotFoundException(id));
  }

  @Override
  public void removeDocument(Long id, boolean isMcp) {
    log.info("Removing document with id [{}]", id);

    documentRepository.findById(id)
        .ifPresent(documentData -> {
          validateAllowMcpWrite(documentData, isMcp);
          documentRepository.delete(documentData);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<DocumentResponse> search(DocumentSearchRequest request) {
    var spec = DocumentSearchSpecifications.withFilters(request);
    return documentRepository.findAll(spec)
        .stream()
        .map(doc -> documentMapper.mapToDocumentResponseLight(
            doc, request.isIncludeContent(), request.isIncludeSummary(), request.isIncludeRelations()))
        .peek(doc -> {
          if (request.isIncludeRelations()) {
            populateWithReferencedBy(doc);
          }
        })
        .toList();
  }

  @Override
  @Transactional
  public void createDocumentRelation(DocumentRelationCreateRequest request, boolean isMcp) {
    log.info("Creating new document relation for [{}] and [{}] with type [{}]",
        request.getDocumentId(), request.getRelatedDocumentId(), request.getType());
    long documentId = request.getDocumentId();
    long relatedDocumentId = request.getRelatedDocumentId();

    documentRepository.findById(documentId)
        .ifPresentOrElse(documentData -> validateAllowMcpWrite(documentData, isMcp), () -> {
          throw new DocumentNotFoundException(documentId);
        });

    if (!documentRepository.existsById(relatedDocumentId)) {
      throw new DocumentNotFoundException(relatedDocumentId);
    }

    var data = documentMapper.mapToDocumentRelationData(request);
    data.setDocument(documentRepository.getReferenceById(documentId));
    data.setRelatedDocument(documentRepository.getReferenceById(relatedDocumentId));

    if (documentRelationRepository.existsById(data.getId())) {
      throw new DocumentRelationExistsException(documentId, relatedDocumentId);
    }

    documentRelationRepository.save(data);
  }

  private void updateProjectIfPresent(String projectKey) {
    if (StringUtils.isBlank(projectKey) || projectRepository.existsByProjectKey(projectKey)) {
      return;
    }

    var project = ProjectData.builder().projectKey(projectKey).build();
    project = projectRepository.save(project);
    log.info("New project [{}] is created with id [{}]", project.getProjectKey(), project.getId());
  }

  private void populateWithReferencedBy(DocumentResponse documentResponse) {
    var referencedBy = documentRelationRepository.findAllByIdRelatedDocumentId(documentResponse.getId())
        .stream().map(documentMapper::mapToReferencingDocumentRelation).toList();
    documentResponse.setReferencedBy(referencedBy);
  }

  private void validateAllowMcpWrite(DocumentData documentData, boolean isMcp) {
    if (!documentData.getAllowMcpWrite() && isMcp) {
      throw new DocumentWriteForbiddenException(documentData.getId());
    }
  }

}
