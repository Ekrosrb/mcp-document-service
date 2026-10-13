package com.ekros.hl.doc.service;

import com.ekros.hl.doc.controller.model.document.*;

import java.util.List;

public interface DocumentService {
  DocumentCreateResponse createDocument(DocumentCreateRequest request);

  void updateDocument(Long id, DocumentUpdateRequest request, boolean isMcp);


  DocumentResponse getDocument(Long id, boolean includeContent);

  void removeDocument(Long id, boolean isMcp);

  List<DocumentResponse> search(DocumentSearchRequest request);

  void createDocumentRelation(DocumentRelationCreateRequest request, boolean isMcp);
}
