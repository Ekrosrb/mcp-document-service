package com.ekros.hl.doc.controller;


import com.ekros.hl.doc.controller.model.GeneralErrorResponse;
import com.ekros.hl.doc.exception.DocumentMemoryTypeChangeException;
import com.ekros.hl.doc.exception.DocumentNotFoundException;
import com.ekros.hl.doc.exception.DocumentRelationExistsException;
import com.ekros.hl.doc.exception.DocumentWriteForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

  private static final String VALIDATION_ERROR_MESSAGE = "Request validation failed";
  private static final String INVALID_VALUE_MESSAGE = "Invalid value: [%s]";

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(exception = DocumentNotFoundException.class)
  public GeneralErrorResponse handleDocumentNotFoundException(DocumentNotFoundException ex) {
    log.warn("DocumentNotFoundException occurred: {}", ex.getMessage());
    return GeneralErrorResponse.builder()
        .message(ex.getMessage())
        .metadata(Map.of("id", ex.getId()))
        .build();
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(exception = DocumentRelationExistsException.class)
  public GeneralErrorResponse handleDocumentRelationExistsException(DocumentRelationExistsException ex) {
    log.warn("DocumentRelationExistsException occurred: {}", ex.getMessage());
    return GeneralErrorResponse.builder()
        .message(ex.getMessage())
        .metadata(Map.of("documentId", ex.getDocumentId(), "relatedDocumentId", ex.getRelatedDocumentId()))
        .build();
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(exception = DocumentWriteForbiddenException.class)
  public GeneralErrorResponse handleDocumentWriteForbiddenException(DocumentWriteForbiddenException ex) {
    log.warn("DocumentWriteForbiddenException occurred: {}", ex.getMessage());
    return GeneralErrorResponse.builder()
        .message(ex.getMessage())
        .metadata(Map.of("id", ex.getId()))
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = DocumentMemoryTypeChangeException.class)
  public GeneralErrorResponse handleDocumentMemoryTypeChangeException(DocumentMemoryTypeChangeException ex) {
    log.warn("DocumentMemoryTypeChangeException occurred: {}", ex.getMessage());
    return GeneralErrorResponse.builder()
        .message(ex.getMessage())
        .metadata(Map.of("id", ex.getId()))
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = MethodArgumentNotValidException.class)
  public GeneralErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.warn("MethodArgumentNotValidException occurred: {}", ex.getMessage());
    var metadata = new LinkedHashMap<String, Object>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      metadata.put(error.getField(), error.getDefaultMessage());
    }
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      metadata.put(error.getObjectName(), error.getDefaultMessage());
    }
    return GeneralErrorResponse.builder()
        .message(VALIDATION_ERROR_MESSAGE)
        .metadata(metadata)
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = HandlerMethodValidationException.class)
  public GeneralErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
    log.warn("HandlerMethodValidationException occurred: {}", ex.getMessage());
    var metadata = new LinkedHashMap<String, Object>();
    for (ParameterValidationResult result : ex.getParameterValidationResults()) {
      if (result instanceof ParameterErrors errors) {
        errors.getFieldErrors().forEach(error -> metadata.put(error.getField(), error.getDefaultMessage()));
        errors.getGlobalErrors().forEach(error -> metadata.put(error.getObjectName(), error.getDefaultMessage()));
        continue;
      }
      for (MessageSourceResolvable error : result.getResolvableErrors()) {
        metadata.put(result.getMethodParameter().getParameterName(), error.getDefaultMessage());
      }
    }
    return GeneralErrorResponse.builder()
        .message(VALIDATION_ERROR_MESSAGE)
        .metadata(metadata)
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = MethodArgumentTypeMismatchException.class)
  public GeneralErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    log.warn("MethodArgumentTypeMismatchException occurred: {}", ex.getMessage());
    return GeneralErrorResponse.builder()
        .message(VALIDATION_ERROR_MESSAGE)
        .metadata(Map.of(ex.getName(), String.format(INVALID_VALUE_MESSAGE, ex.getValue())))
        .build();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public GeneralErrorResponse handleException(Exception ex) {
    log.error("Exception occurred: {}", ex.getMessage(), ex);
    return GeneralErrorResponse.builder()
        .message(ex.getMessage())
        .build();
  }
}
