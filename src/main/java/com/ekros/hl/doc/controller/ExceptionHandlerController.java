package com.ekros.hl.doc.controller;


import com.ekros.hl.doc.controller.model.GeneralErrorResponse;
import com.ekros.hl.doc.exception.DocumentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

  private static final String VALIDATION_ERROR_MESSAGE = "Request validation failed";

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(exception = DocumentNotFoundException.class)
  public GeneralErrorResponse handleDocumentNotFoundException(DocumentNotFoundException ex) {
    log.warn("DocumentNotFoundException occurred: {}", ex.getMessage());
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
      for (MessageSourceResolvable error : result.getResolvableErrors()) {
        metadata.put(result.getMethodParameter().getParameterName(), error.getDefaultMessage());
      }
    }
    return GeneralErrorResponse.builder()
        .message(VALIDATION_ERROR_MESSAGE)
        .metadata(metadata)
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
