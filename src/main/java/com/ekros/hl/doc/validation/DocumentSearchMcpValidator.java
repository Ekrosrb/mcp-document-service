package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.mcp.model.DocumentSearchMcpRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentSearchMcpValidator implements ConstraintValidator<DocumentSearchValid, DocumentSearchMcpRequest> {

  @Override
  public boolean isValid(DocumentSearchMcpRequest value, ConstraintValidatorContext context) {
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

  private boolean isValid(DocumentSearchMcpRequest request) {
    return request.getKind() != null
        || request.getScope() != null
        || request.getAuthor() != null
        || StringUtils.isNotBlank(request.getProjectKey())
        || StringUtils.isNotBlank(request.getAuthorName())
        || StringUtils.isNotBlank(request.getQuery());
  }
}
