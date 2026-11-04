package io.github.ekrosrb.doc.validation;

import io.github.ekrosrb.doc.controller.model.document.DocumentSearchRequest;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentSearchValidator implements ConstraintValidator<DocumentSearchValid, DocumentSearchRequest> {

  @Override
  public boolean isValid(DocumentSearchRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      if (value.getMemoryType() != null && value.getKind() == DocumentKind.DOCUMENT) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Memory type can be used only with kind: [MEMORY]")
            .addPropertyNode("memoryType")
            .addConstraintViolation();
        return false;
      }

    } catch (Exception e) {
      log.error("Unexpected validation exception: {}", e.getMessage(), e);
      return false;
    }

    return true;
  }
}
