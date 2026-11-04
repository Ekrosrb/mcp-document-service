package io.github.ekrosrb.doc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {DocumentRelationCreateValidator.class, DocumentRelationRemoveValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentRelationValid {

  String message() default "Invalid document relation";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
