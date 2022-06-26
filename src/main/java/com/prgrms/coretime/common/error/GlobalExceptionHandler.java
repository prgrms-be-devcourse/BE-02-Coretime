package com.prgrms.coretime.common.error;


import com.prgrms.coretime.common.error.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  // RequestBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.warn(e.getMessage());
    return ResponseEntity.badRequest().body("");
  }

  // ModelAttribute
  @ExceptionHandler(org.springframework.validation.BindException.class)
  public ResponseEntity<String> handleBindException(BindException e) {
    log.warn(e.getMessage());
    return ResponseEntity.badRequest().body("");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.warn(e.getMessage());
    return ResponseEntity.badRequest().body("");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn(e.getMessage());
    return ResponseEntity.badRequest().body("");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
    log.warn(e.getMessage());
    return ResponseEntity.badRequest().body("");
  }
}
