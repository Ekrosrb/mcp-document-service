package com.ekros.hl.doc.controller;

import com.ekros.hl.doc.controller.api.DocumentApi;
import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.controller.model.DocumentSearchRequest;
import com.ekros.hl.doc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
  public void removeDocument(@PathVariable String id) {
    documentService.removeDocument(id);
  }

  @Override
  @GetMapping("/{id}")
  public DocumentResponse getDocument(@PathVariable String id, @RequestParam(required = false, defaultValue = "false") boolean includeContent) {
    return documentService.getDocument(id, includeContent);
  }

  @Override
  @PostMapping("/search")
  public List<DocumentResponse> search(@RequestBody DocumentSearchRequest request) {
    return documentService.search(request);
  }
}
