package com.ekros.hl.doc.controller.api;

import com.ekros.hl.doc.controller.model.document.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DocumentApi {

  DocumentCreateResponse createDocument(@Valid @RequestBody DocumentCreateRequest request);

  void updateDocument(@Positive Long id, @Valid @RequestBody DocumentUpdateRequest request);

  void removeDocument(@Positive @PathVariable Long id);

  DocumentResponse getDocument(@Positive @PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean includeContent);

  List<DocumentResponse> search(@Valid @RequestBody DocumentSearchRequest request);

  void createDocumentRelation(@Valid @RequestBody DocumentRelationCreateRequest request);

  void removeDocumentRelation(@Valid DocumentRelationRemoveRequest request);
}
