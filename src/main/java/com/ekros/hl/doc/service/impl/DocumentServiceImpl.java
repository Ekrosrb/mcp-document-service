package com.ekros.hl.doc.service.impl;

import com.ekros.hl.doc.exception.DocumentNotFoundException;
import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.controller.model.DocumentSearchRequest;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.repository.DocumentRepository;
import com.ekros.hl.doc.repository.specification.DocumentSearchSpecifications;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final DocumentMapper documentMapper;

  @Override
  public DocumentCreateResponse createDocument(DocumentCreateRequest request) {
    log.info("Creating new document with kind [{}], scope [{}] and author [{}]", request.getKind(), request.getScope(), request.getAuthor());
    var documentData = documentMapper.mapToDocumentData(request);
    documentData = documentRepository.save(documentData);
    log.info("Document saved with id [{}]", documentData.getId());
    return documentMapper.mapToDocumentCreateResponse(documentData);
  }

  @Override
  public DocumentResponse getDocument(String id) {
    return documentRepository.findById(UUID.fromString(id))
        .map(documentMapper::mapToDocumentResponse)
        .orElseThrow(() -> new DocumentNotFoundException(id));
  }

  @Override
  public void removeDocument(String id) {
    log.info("Removing document with id [{}]", id);
    documentRepository.deleteById(UUID.fromString(id));
  }

  @Override
  public List<DocumentResponse> search(DocumentSearchRequest request) {
    var spec = DocumentSearchSpecifications.withFilters(request);
    return documentRepository.findAll(spec)
        .stream()
        .map(documentData -> documentMapper.mapToDocumentResponseLight(documentData, request.isIncludeContent(), request.isIncludeSummary()))
        .toList();
  }

}
