package com.ekros.hl.doc.validation;

import com.ekros.hl.doc.mcp.model.DocumentUpdateMcpRequest;
import com.ekros.hl.doc.repository.model.DocumentType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentUpdateMcpValidator implements ConstraintValidator<DocumentUpdateValid, DocumentUpdateMcpRequest> {

  @Override
  public boolean isValid(DocumentUpdateMcpRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    if (value.getType() == DocumentType.CUSTOM && StringUtils.isBlank(value.getCustomExtension())) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Document customExtension must be specified for type: [CUSTOM]")
          .addPropertyNode("customExtension")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
