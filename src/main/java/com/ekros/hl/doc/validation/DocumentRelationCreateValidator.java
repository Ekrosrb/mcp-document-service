package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.controller.model.document.DocumentRelationCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentRelationCreateValidator
    implements ConstraintValidator<DocumentRelationValid, DocumentRelationCreateRequest> {

  @Override
  public boolean isValid(DocumentRelationCreateRequest value, ConstraintValidatorContext context) {
    if (value == null || value.getDocumentId() != value.getRelatedDocumentId()) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate("Document cannot be related to itself")
        .addPropertyNode("relatedDocumentId")
        .addConstraintViolation();
    return false;
  }
}
