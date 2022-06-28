package com.prgrms.coretime.common.error;

public class FriendAlreadyExistsException extends RuntimeException{

  public FriendAlreadyExistsException() {
  }

  public FriendAlreadyExistsException(String message) {
    super(message);
  }
}
