package io.github.ekrosrb.doc.service.impl;

import io.github.ekrosrb.doc.controller.model.document.*;
import io.github.ekrosrb.doc.exception.DocumentMemoryTypeChangeException;
import io.github.ekrosrb.doc.exception.DocumentNotFoundException;
import io.github.ekrosrb.doc.exception.DocumentRelationExistsException;
import io.github.ekrosrb.doc.exception.DocumentWriteForbiddenException;
import io.github.ekrosrb.doc.mapper.DocumentMapper;
import io.github.ekrosrb.doc.repository.DocumentRelationRepository;
import io.github.ekrosrb.doc.repository.DocumentRepository;
import io.github.ekrosrb.doc.repository.ProjectRepository;
import io.github.ekrosrb.doc.repository.model.DocumentData;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import io.github.ekrosrb.doc.repository.projection.DocumentRankProjection;
import io.github.ekrosrb.doc.repository.specification.DocumentSearchSpecifications;
import io.github.ekrosrb.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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

  private static final Sort DELETED_ORDER = Sort.by(Sort.Order.desc("deletedAt"), Sort.Order.desc("id"));

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
    var documentData = documentRepository.findByIdAndDeletedAtIsNull(id)
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
    return documentRepository.findByIdAndDeletedAtIsNull(id)
        .map(doc -> mapToDocumentResponse(doc, includeContent))
        .orElseThrow(() -> new DocumentNotFoundException(id));
  }

  @Override
  @Transactional
  public void removeDocument(Long id, boolean isMcp) {
    log.info("Marking document with id [{}] as deleted", id);

    documentRepository.findByIdAndDeletedAtIsNull(id)
        .ifPresent(documentData -> {
          validateAllowMcpWrite(documentData, isMcp);
          documentRepository.markDeleted(id, Timestamp.from(Instant.now()));
        });
  }

  @Override
  @Transactional
  public void removeDocumentPermanently(Long id) {
    log.info("Removing document with id [{}] permanently", id);
    documentRepository.findById(id).ifPresent(documentRepository::delete);
  }

  @Override
  @Transactional
  public void restoreDocument(Long id) {
    log.info("Restoring document with id [{}]", id);
    if (!documentRepository.existsById(id)) {
      throw new DocumentNotFoundException(id);
    }
    documentRepository.clearDeleted(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentResponse> getDeletedDocuments(Pageable pageable) {
    var page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DELETED_ORDER);
    return documentRepository.findAllByDeletedAtIsNotNull(page)
        .map(doc -> documentMapper.mapToDocumentResponseLight(doc, false, true, false));
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentResponse getDeletedDocument(Long id, boolean includeContent) {
    return documentRepository.findByIdAndDeletedAtIsNotNull(id)
        .map(doc -> mapToDocumentResponse(doc, includeContent))
        .orElseThrow(() -> new DocumentNotFoundException(id));
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
    var matches = keepStrongMatches(findDocuments(matchRequest,
        DocumentSearchSpecifications.withoutOtherProjects(request.getProjectKey()), request.getLimit()), query.get());
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

    documentRepository.findByIdAndDeletedAtIsNull(documentId)
        .ifPresentOrElse(documentData -> validateAllowMcpWrite(documentData, isMcp), () -> {
          throw new DocumentNotFoundException(documentId);
        });

    if (!documentRepository.existsByIdAndDeletedAtIsNull(relatedDocumentId)) {
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
   * Leaves out matches ranked below a share of the best match's rank, keeping the order.
   */
  private List<DocumentData> keepStrongMatches(List<DocumentData> matches, String query) {
    if (matches.isEmpty()) {
      return matches;
    }
    var ranks = documentRepository.findRanks(matches.stream().map(DocumentData::getId).toList(), query).stream()
        .collect(Collectors.toMap(DocumentRankProjection::getId, DocumentRankProjection::getRank));
    var minRank = Collections.max(ranks.values()) * DocumentRecallRequest.MIN_RELATIVE_RANK;
    return matches.stream()
        .filter(doc -> ranks.get(doc.getId()) >= minRank)
        .toList();
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
    if (!documentRepository.existsByIdAndDeletedAtIsNull(id)) {
      throw new DocumentNotFoundException(id);
    }
    return documentRepository.getReferenceById(id);
  }

  private void updateProjectIfPresent(String projectKey) {
    if (StringUtils.isNotBlank(projectKey) && projectRepository.insertIfAbsent(projectKey) > 0) {
      log.info("New project [{}] is created", projectKey);
    }
  }

  private DocumentResponse mapToDocumentResponse(DocumentData documentData, boolean includeContent) {
    var response = documentMapper.mapToDocumentResponse(documentData, includeContent);
    populateWithReferencedBy(response);
    return response;
  }

  private void populateWithReferencedBy(DocumentResponse documentResponse) {
    var referencedBy = documentRelationRepository.findAllByIdRelatedDocumentIdAndDocumentDeletedAtIsNull(documentResponse.getId())
        .stream().map(documentMapper::mapToReferencingDocumentRelation).toList();
    documentResponse.setReferencedBy(referencedBy);
  }

  private void validateAllowMcpWrite(DocumentData documentData, boolean isMcp) {
    if (!documentData.getAllowMcpWrite() && isMcp) {
      throw new DocumentWriteForbiddenException(documentData.getId());
    }
  }

}
