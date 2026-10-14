package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.controller.model.document.DocumentRelationRemoveRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentRelationRemoveValidator
    implements ConstraintValidator<DocumentRelationValid, DocumentRelationRemoveRequest> {

  @Override
  public boolean isValid(DocumentRelationRemoveRequest value, ConstraintValidatorContext context) {
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
