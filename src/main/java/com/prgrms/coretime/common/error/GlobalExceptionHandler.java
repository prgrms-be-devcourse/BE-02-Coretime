package com.prgrms.coretime.common.error;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  // RequestBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    return ResponseEntity.badRequest().body("");
  }

  // ModelAttribute
  @ExceptionHandler(org.springframework.validation.BindException.class)
  public ResponseEntity<String> handleBindException(BindException e) {
    return ResponseEntity.badRequest().body("");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    return ResponseEntity.badRequest().body("");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body("");
  }

  /**
   * TODO: 수정 필요
   */

  @ExceptionHandler(FriendAlreadyExistsException.class)
  public ResponseEntity<String> handleFriendExistsException(FriendAlreadyExistsException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("FriendAlreadyExistsException");
  }

  @ExceptionHandler(DuplicateFriendRequestException.class)
  public ResponseEntity<String> handleDuplicateFriendRequestException(DuplicateFriendRequestException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("FriendAlreadyExistsException");
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("InvalidRequestException");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("NotFoundException");
  }
}
