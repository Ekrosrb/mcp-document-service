package com.ekros.hl.doc.service;

import com.ekros.hl.doc.controller.model.document.*;

import java.util.List;

public interface DocumentService {
  DocumentCreateResponse createDocument(DocumentCreateRequest request);

  DocumentResponse getDocument(Long id, boolean includeContent);

  void removeDocument(Long id);

  List<DocumentResponse> search(DocumentSearchRequest request);

  void createDocumentRelation(DocumentRelationCreateRequest request);
}
