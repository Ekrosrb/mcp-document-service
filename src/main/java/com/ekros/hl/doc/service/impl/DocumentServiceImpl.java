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
import com.ekros.hl.doc.repository.specification.DocumentSearchSpecifications;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    updateProjectIfPresent(request.getProjectKey());

    documentMapper.updateDocument(documentData, request);
    documentData.setUpdatedAt(Timestamp.from(Instant.now()));
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
  @Transactional(readOnly = true)
  public DocumentRecallResponse recall(DocumentRecallRequest request) {
    log.info("Recalling context for project [{}] with depth [{}]", request.getProjectKey(), request.getDepth());
    var query = DocumentSearchSpecifications.buildFreeTextQuery(request.getTask());
    if (query.isEmpty()) {
      return DocumentRecallResponse.builder().matches(List.of()).linked(List.of()).build();
    }

    var matchRequest = DocumentSearchRequest.builder().query(query.get()).build();
    var matches = findDocuments(matchRequest,
        DocumentSearchSpecifications.withoutOtherProjects(request.getProjectKey()), request.getLimit());
    var linked = findLinkedDocuments(matches, request.getDepth(), request.getLimit());

    return DocumentRecallResponse.builder()
        .matches(mapToRecallResponses(matches, request.isIncludeContent()))
        .linked(mapToRecallResponses(linked, request.isIncludeContent()))
        .build();
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

  private List<DocumentData> findDocuments(DocumentSearchRequest request, Specification<DocumentData> restriction, int limit) {
    var spec = DocumentSearchSpecifications.withFilters(request)
        .and(restriction)
        .and(DocumentSearchSpecifications.withOrder(request));
    return documentRepository.findAll(spec, PageRequest.of(0, limit)).getContent();
  }

  /**
   * Returns the current entries linked in either direction to the given ones, up to the given number of hops,
   * nearest first and then most recently updated first.
   */
  private List<DocumentData> findLinkedDocuments(List<DocumentData> documents, int depth, int limit) {
    var visited = documents.stream().map(DocumentData::getId).collect(Collectors.toCollection(HashSet::new));
    Set<Long> frontier = Set.copyOf(visited);
    var linked = new ArrayList<DocumentData>();

    for (var hop = 0; hop < depth && !frontier.isEmpty() && linked.size() < limit; hop++) {
      var neighbourIds = documentRelationRepository.findAllByIdDocumentIdInOrIdRelatedDocumentIdIn(frontier, frontier)
          .stream()
          .flatMap(relation -> Stream.of(relation.getId().getDocumentId(), relation.getId().getRelatedDocumentId()))
          .collect(Collectors.toCollection(HashSet::new));
      neighbourIds.removeAll(visited);
      visited.addAll(neighbourIds);
      if (neighbourIds.isEmpty()) {
        break;
      }

      var hopDocuments = findDocuments(DocumentSearchRequest.builder().build(),
          DocumentSearchSpecifications.withIds(neighbourIds), limit - linked.size());
      linked.addAll(hopDocuments);
      frontier = hopDocuments.stream().map(DocumentData::getId).collect(Collectors.toSet());
    }
    return linked;
  }

  private List<DocumentResponse> mapToRecallResponses(List<DocumentData> documents, boolean includeContent) {
    return documents.stream()
        .map(doc -> {
          var response = documentMapper.mapToDocumentResponseLight(doc, includeContent, true, true);
          populateWithReferencedBy(response);
          return response;
        })
        .toList();
  }

  private DocumentData getExistingDocumentReference(Long id) {
    if (!documentRepository.existsById(id)) {
      throw new DocumentNotFoundException(id);
    }
    return documentRepository.getReferenceById(id);
  }

  private void updateProjectIfPresent(String projectKey) {
    if (StringUtils.isNotBlank(projectKey) && projectRepository.insertIfAbsent(projectKey) > 0) {
      log.info("New project [{}] is created", projectKey);
    }
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
