package io.github.ekrosrb.doc.service;

import io.github.ekrosrb.doc.controller.model.document.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {
  DocumentCreateResponse createDocument(DocumentCreateRequest request);

  void updateDocument(Long id, DocumentUpdateRequest request, boolean isMcp);


  DocumentResponse getDocument(Long id, boolean includeContent);

  void removeDocument(Long id, boolean isMcp);

  void removeDocumentPermanently(Long id);

  void restoreDocument(Long id);

  Page<DocumentResponse> getDeletedDocuments(Pageable pageable);

  DocumentResponse getDeletedDocument(Long id, boolean includeContent);

  Page<DocumentResponse> search(DocumentSearchRequest request, Pageable pageable);

  DocumentRecallResponse recall(DocumentRecallRequest request);

  void createDocumentRelation(DocumentRelationCreateRequest request, boolean isMcp);

  void removeDocumentRelation(DocumentRelationRemoveRequest request, boolean isMcp);
}
