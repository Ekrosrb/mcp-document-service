package io.github.ekrosrb.doc.validation;

import io.github.ekrosrb.doc.controller.model.document.DocumentUpdateRequest;
import io.github.ekrosrb.doc.repository.model.DocumentType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentUpdateValidator implements ConstraintValidator<DocumentUpdateValid, DocumentUpdateRequest> {

  @Override
  public boolean isValid(DocumentUpdateRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      if (value.getType() == DocumentType.CUSTOM && StringUtils.isBlank(value.getCustomExtension())) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Document customExtension must be specified for type: [CUSTOM]")
            .addPropertyNode("customExtension")
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
