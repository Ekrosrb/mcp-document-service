package com.ekros.hl.doc.service;

import com.ekros.hl.doc.controller.model.document.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {
  DocumentCreateResponse createDocument(DocumentCreateRequest request);

  void updateDocument(Long id, DocumentUpdateRequest request, boolean isMcp);


  DocumentResponse getDocument(Long id, boolean includeContent);

  void removeDocument(Long id, boolean isMcp);

  Page<DocumentResponse> search(DocumentSearchRequest request, Pageable pageable);

  void createDocumentRelation(DocumentRelationCreateRequest request, boolean isMcp);

  void removeDocumentRelation(DocumentRelationRemoveRequest request, boolean isMcp);
}
