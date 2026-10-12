package com.ekros.hl.doc.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      UUID.fromString(value);
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(String.format("Invalid UUID: [%s]", value))
          .addConstraintViolation();
      return false;
    }
  }
}
