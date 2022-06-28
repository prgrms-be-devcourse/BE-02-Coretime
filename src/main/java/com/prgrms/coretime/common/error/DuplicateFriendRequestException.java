package com.prgrms.coretime.common.error;

public class DuplicateFriendRequestException extends RuntimeException{

  public DuplicateFriendRequestException() {
  }

  public DuplicateFriendRequestException(String message) {
    super(message);
  }
}
