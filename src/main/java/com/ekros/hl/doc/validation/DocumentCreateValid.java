package com.ekros.hl.doc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {DocumentCreateValidator.class, DocumentCreateMcpValidator.class})
@Target({ ElementType.TYPE }) 
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentCreateValid {
    
    String message() default "The fields do not match!"; // Default message
    
    Class<?>[] groups() default {}; // Required by the Bean Validation spec
    
    Class<? extends Payload>[] payload() default {}; // Required by the Bean Validation spec

    // Allows using multiple @FieldsMatch annotations on the same class
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        DocumentCreateValid[] value();
    }
}