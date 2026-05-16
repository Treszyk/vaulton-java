package dev.vaulton.vaultonapi.infrastructure.web;

import dev.vaulton.vaultonapi.application.dto.shared.ErrorResponse;
import dev.vaulton.vaultonapi.domain.exception.VaultonDomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(VaultonDomainException.class)
  public ResponseEntity<ErrorResponse> handleDomainException(VaultonDomainException ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse(ex.getPublicMessage()));
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleParsingException(Exception ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request format."));
  }
}
