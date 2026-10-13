package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentCreateValidator implements ConstraintValidator<DocumentCreateValid, DocumentCreateRequest> {

  @Override
  public boolean isValid(DocumentCreateRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      if (value.getScope() == DocumentScope.PROJECT && StringUtils.isBlank(value.getProjectKey())) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Project key must be specified for scope: [PROJECT]")
            .addPropertyNode("projectKey")
            .addConstraintViolation();
        return false;
      }

      if(value.getType() == DocumentType.CUSTOM && StringUtils.isBlank(value.getCustomExtension())) {
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
