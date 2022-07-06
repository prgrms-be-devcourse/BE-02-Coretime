package com.prgrms.coretime.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum ErrorCode {

  /**
   * Code 팀원들끼리 정해야하는데 이거 관리해야하나여..?ㅠㅠ 깃모지 될 것 같은데...
   */

  //Internal Server Error
  INTERNAL_SERVER_ERROR(500, "server01", "서버에 문제가 생겼습니다."),

  // 400 Client Error
  METHOD_NOT_ALLOWED(405, "client01", "적절하지 않은 HTTP 메소드입니다."),
  INVALID_TYPE_VALUE(400, "client02", "요청 값의 타입이 잘못되었습니다."),
  INVALID_INPUT_VALUE(400, "client03", "적절하지 않은 요청값입니다."),
  NOT_FOUND(404, "client04", "해당 리소스를 찾을 수 없습니다."),
  USER_NOT_FOUND(500, "U001", "유저가 존재하지 않습니다."),

  /**
   * Friend Domain
   */
  INVALID_FRIEND_REQUEST_TARGET(400, "F001", "올바르지 않은 친구 상대입니다."),
  DUPLICATE_FRIEND_REQUEST(400, "F002", "이미 완료된 친구 요청입니다."),
  FRIEND_ALREADY_EXISTS(400, "F003", "이미 등록된 친구입니다."),
  FRIEND_NOT_FOUND(404, "F004", "해당 friend 리소스를 찾을 수 없습니다."),

  /**
   * Message Domain
   */
  MESSAGE_ROOM_NOT_FOUND(404, "M001", "해당 쪽지방 리소스를 찾을 수 없습니다."),
  NO_PERMISSION_TO_SEND_MESSAGE(404, "M002", "해당 쪽지방에 쪽지를 보낼 수 없습니다."),
  INVALID_MESSAGE_TARGET(400, "M003", "올바르지 않은 쪽지 상대입니다."),
  UNABLE_TO_SEND_MESSAGE(409, "M004", "쪽지를 보낼 수 없는 상대입니다."),
  NO_PERMISSION_TO_MODIFY_MESSAGE_ROOM(404, "M005", "해당 쪽지방을 수정할 수 없습니다."),
  NO_PERMISSION_TO_READ_DATA(409, "M006", "삭제된 쪽지방입니다.");

  private final int status;
  private final String code;
  private final String message;

  ErrorCode(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  private static final Map<String, ErrorCode> messageMap
      = Collections.unmodifiableMap(Stream.of(values())
      .collect(Collectors.toMap(ErrorCode::getMessage, Function.identity())));

  public static ErrorCode fromMessage(String message) {
    return messageMap.get(message);
  }

}
