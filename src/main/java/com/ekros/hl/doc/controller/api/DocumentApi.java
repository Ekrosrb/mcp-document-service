package com.ekros.hl.doc.controller.api;

import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.controller.model.DocumentSearchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface DocumentApi {

  DocumentCreateResponse createDocument(@Valid DocumentCreateRequest request);

  void removeDocument(@Positive Long id);

  DocumentResponse getDocument(@Positive Long id, boolean includeContent);

  List<DocumentResponse> search(@Valid DocumentSearchRequest request);

}
