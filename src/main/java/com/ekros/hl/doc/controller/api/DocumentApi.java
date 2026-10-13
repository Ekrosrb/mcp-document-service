package com.ekros.hl.doc.controller.api;

import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.controller.model.DocumentSearchRequest;
import com.ekros.hl.doc.validation.ValidUUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface DocumentApi {

  DocumentCreateResponse createDocument(@Valid DocumentCreateRequest request);

  void removeDocument(@ValidUUID @NotBlank String id);

  DocumentResponse getDocument(@ValidUUID @NotBlank String id, boolean includeContent);

  List<DocumentResponse> search(@Valid DocumentSearchRequest request);

}
