package com.ekros.hl.doc.controller.api;

import com.ekros.hl.doc.controller.model.document.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface DocumentApi {

  DocumentCreateResponse createDocument(@Valid @RequestBody DocumentCreateRequest request);

  void updateDocument(@Positive Long id, @Valid @RequestBody DocumentUpdateRequest request);

  void removeDocument(@Positive @PathVariable Long id);

  void removeDocumentPermanently(@Positive @PathVariable Long id);

  void restoreDocument(@Positive @PathVariable Long id);

  Page<DocumentResponse> getDeletedDocuments(@PageableDefault(size = DocumentSearchRequest.DEFAULT_PAGE_SIZE) Pageable pageable);

  DocumentResponse getDeletedDocument(@Positive @PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean includeContent);

  DocumentResponse getDocument(@Positive @PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean includeContent);

  Page<DocumentResponse> search(@Valid @RequestBody DocumentSearchRequest request,
                                @PageableDefault(size = DocumentSearchRequest.DEFAULT_PAGE_SIZE) Pageable pageable);

  void createDocumentRelation(@Valid @RequestBody DocumentRelationCreateRequest request);

  void removeDocumentRelation(@Valid DocumentRelationRemoveRequest request);
}
