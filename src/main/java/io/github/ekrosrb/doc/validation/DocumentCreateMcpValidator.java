package io.github.ekrosrb.doc.validation;

import io.github.ekrosrb.doc.mcp.model.DocumentCreateMcpRequest;
import io.github.ekrosrb.doc.repository.model.DocumentScope;
import io.github.ekrosrb.doc.repository.model.DocumentType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

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

    if (value.getType() == DocumentType.CUSTOM && StringUtils.isBlank(value.getCustomExtension())) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Document customExtension must be specified for type: [CUSTOM]")
          .addPropertyNode("customExtension")
          .addConstraintViolation();
      return false;
    }

    if (hasDuplicateRelatedDocuments(value.getRelations())) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Each related document can be listed only once")
          .addPropertyNode("relations")
          .addConstraintViolation();
      return false;
    }

    return true;
  }

  private boolean hasDuplicateRelatedDocuments(List<DocumentCreateMcpRequest.DocumentRelation> relations) {
    return relations != null && relations.stream()
        .map(DocumentCreateMcpRequest.DocumentRelation::getRelatedDocumentId)
        .distinct()
        .count() != relations.size();
  }
}
