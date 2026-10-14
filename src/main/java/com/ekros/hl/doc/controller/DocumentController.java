package com.ekros.hl.doc.controller;

import com.ekros.hl.doc.controller.api.DocumentApi;
import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

  private final DocumentService documentService;

  @Override
  @PostMapping
  public DocumentCreateResponse createDocument(@Valid @RequestBody DocumentCreateRequest request) {
    return documentService.createDocument(request);
  }

  @Override
  @ResponseStatus(OK)
  @PutMapping("/{id}")
  public void updateDocument(@Positive @PathVariable Long id, @Valid @RequestBody DocumentUpdateRequest request) {
    documentService.updateDocument(id, request, false);
  }

  @Override
  @ResponseStatus(OK)
  @DeleteMapping("/{id}")
  public void removeDocument(@Positive @PathVariable Long id) {
    documentService.removeDocument(id, false);
  }

  @Override
  @GetMapping("/{id}")
  public DocumentResponse getDocument(@Positive @PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean includeContent) {
    return documentService.getDocument(id, includeContent);
  }

  @Override
  @PostMapping("/search")
  public List<DocumentResponse> search(@Valid @RequestBody DocumentSearchRequest request) {
    return documentService.search(request);
  }

  @Override
  @ResponseStatus(CREATED)
  @PostMapping("/relation")
  public void createDocumentRelation(@Valid @RequestBody DocumentRelationCreateRequest request) {
    documentService.createDocumentRelation(request, false);
  }

  @Override
  @ResponseStatus(OK)
  @DeleteMapping("/relation/{documentId}/{relatedDocumentId}")
  public void removeDocumentRelation(@Valid DocumentRelationRemoveRequest request) {
    documentService.removeDocumentRelation(request, false);
  }
}
