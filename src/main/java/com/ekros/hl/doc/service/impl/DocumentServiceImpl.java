package com.ekros.hl.doc.service.impl;

import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.exception.DocumentMemoryTypeChangeException;
import com.ekros.hl.doc.exception.DocumentNotFoundException;
import com.ekros.hl.doc.exception.DocumentRelationExistsException;
import com.ekros.hl.doc.exception.DocumentWriteForbiddenException;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.repository.DocumentRelationRepository;
import com.ekros.hl.doc.repository.DocumentRepository;
import com.ekros.hl.doc.repository.ProjectRepository;
import com.ekros.hl.doc.repository.model.DocumentData;
import com.ekros.hl.doc.repository.model.DocumentKind;
import com.ekros.hl.doc.repository.model.ProjectData;
import com.ekros.hl.doc.repository.specification.DocumentSearchSpecifications;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    var savedDocumentData = documentRepository.save(documentData);

    var relations = Optional.ofNullable(request.getRelations()).orElseGet(List::of).stream()
        .map(relation -> documentMapper.mapToDocumentRelationData(
            savedDocumentData, getExistingDocumentReference(relation.getRelatedDocumentId()), relation))
        .toList();
    documentRelationRepository.saveAll(relations);

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

    if (request.getMemoryType() != null && documentData.getKind() != DocumentKind.MEMORY) {
      throw new DocumentMemoryTypeChangeException(documentData.getId());
    }

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
  public Page<DocumentResponse> search(DocumentSearchRequest request, Pageable pageable) {
    var page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    var documents = findDocuments(request, page);

    if (documents.getTotalElements() == 0) {
      var anyWordQuery = DocumentSearchSpecifications.buildAnyWordQuery(request.getQuery());
      if (anyWordQuery.isPresent()) {
        log.info("No document matches all words of [{}], searching for any of them", request.getQuery());
        documents = findDocuments(request.toBuilder().query(anyWordQuery.get()).build(), page);
      }
    }

    return documents
        .map(doc -> {
          var response = documentMapper.mapToDocumentResponseLight(
              doc, request.isIncludeContent(), request.isIncludeSummary(), request.isIncludeRelations());
          if (request.isIncludeRelations()) {
            populateWithReferencedBy(response);
          }
          return response;
        });
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

  @Override
  public void removeDocumentRelation(DocumentRelationRemoveRequest request, boolean isMcp) {
    log.info("Removing document relation for [{}] and [{}]", request.getDocumentId(), request.getRelatedDocumentId());
    long documentId = request.getDocumentId();
    documentRepository.findById(documentId)
        .ifPresent(documentData -> validateAllowMcpWrite(documentData, isMcp));

    var id = documentMapper.mapToDocumentRelationId(request);
    documentRelationRepository.deleteById(id);
  }

  private Page<DocumentData> findDocuments(DocumentSearchRequest request, Pageable page) {
    var spec = DocumentSearchSpecifications.withFilters(request)
        .and(DocumentSearchSpecifications.withOrder(request));
    return documentRepository.findAll(spec, page);
  }

  private DocumentData getExistingDocumentReference(Long id) {
    if (!documentRepository.existsById(id)) {
      throw new DocumentNotFoundException(id);
    }
    return documentRepository.getReferenceById(id);
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
