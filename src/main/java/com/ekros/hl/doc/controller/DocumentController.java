package com.ekros.hl.doc.controller;

import com.ekros.hl.doc.controller.api.DocumentApi;
import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

  private final DocumentService documentService;

  @Override
  @PostMapping
  public DocumentCreateResponse createDocument(@RequestBody DocumentCreateRequest request) {
    return documentService.createDocument(request);
  }

  @Override
  @DeleteMapping("/{id}")
  public void removeDocument(@PathVariable Long id) {
    documentService.removeDocument(id);
  }

  @Override
  @GetMapping("/{id}")
  public DocumentResponse getDocument(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean includeContent) {
    return documentService.getDocument(id, includeContent);
  }

  @Override
  @PostMapping("/search")
  public List<DocumentResponse> search(@RequestBody DocumentSearchRequest request) {
    return documentService.search(request);
  }

  @Override
  @ResponseStatus(CREATED)
  @PostMapping("/relation")
  public void createDocumentRelation(@RequestBody DocumentRelationCreateRequest request) {
    documentService.createDocumentRelation(request);
  }
}
