package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.mcp.model.DocumentCreateMcpRequest;
import com.ekros.hl.doc.repository.model.DocumentScope;
import com.ekros.hl.doc.repository.model.DocumentType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentCreateMcpValidator implements ConstraintValidator<DocumentCreateValid, DocumentCreateMcpRequest> {

  @Override
  public boolean isValid(DocumentCreateMcpRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

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

    return true;
  }
}
