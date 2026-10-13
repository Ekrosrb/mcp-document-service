package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.controller.model.document.DocumentSearchRequest;
import io.micrometer.common.util.StringUtils;
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
      if (!isValid(value)) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("At least one property or query in search request must be specified")
            .addConstraintViolation();
        return false;
      }

    } catch (Exception e) {
      log.error("Unexpected validation exception: {}", e.getMessage(), e);
      return false;
    }

    return true;
  }

  private boolean isValid(DocumentSearchRequest request) {
    return request.getKind() != null
        || request.getScope() != null
        || request.getAuthor() != null
        || StringUtils.isNotBlank(request.getProjectKey())
        || StringUtils.isNotBlank(request.getAuthorName())
        || StringUtils.isNotBlank(request.getQuery());
  }
}
