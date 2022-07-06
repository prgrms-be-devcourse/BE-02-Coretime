package com.prgrms.coretime.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum ErrorCode {

  //Internal Server Error
  INTERNAL_SERVER_ERROR(500, "server01", "서버에 문제가 생겼습니다."),

  // 400 Client Error
  METHOD_NOT_ALLOWED(405, "C001", "적절하지 않은 HTTP 메소드입니다."),
  INVALID_TYPE_VALUE(400, "C002", "요청 값의 타입이 잘못되었습니다."),
  INVALID_INPUT_VALUE(400, "C003", "적절하지 않은 값입니다."),
  NOT_FOUND(404, "C004", "해당 리소스를 찾을 수 없습니다."),
  BAD_REQUEST(400, "C005", "잘못된 요청입니다."),
  MISSING_REQUEST_PARAMETER(400, "C005", "필수 파라미터가 누락되었습니다."),
  INVALID_LENGTH(400, "C006", "올바르지 않은 길이입니다."),

  /**
   * School Domain
   */
  SCHOOL_NOT_FOUND(400, "S001", "학교가 존재하지 않습니다."),
  SCHOOL_AUTH_FAILED(400, "S002", "학교 인증에 실패했습니다."),

  /**
   * User Domain
   */
  USER_NOT_FOUND(400, "U001", "유저가 존재하지 않습니다."),
  INVALID_ACCOUNT_REQUEST(400, "U002", "아이디 및 비밀번호가 올바르지 않습니다."),
  INVALID_TOKEN_REQUEST(400, "U003", "토큰이 올바르지 않습니다."),
  USER_ALREADY_EXISTS(400, "U004", "유저가 이미 존재합니다."),

  /**
   * Friend Domain
   */
  INVALID_FRIEND_REQUEST_TARGET(400, "F001", "올바르지 않은 친구 상대입니다."),
  DUPLICATE_FRIEND_REQUEST(400, "F002", "이미 완료된 친구 요청입니다."),
  FRIEND_ALREADY_EXISTS(400, "F003", "이미 등록된 친구입니다."),
  FRIEND_NOT_FOUND(404, "F004", "해당 friend 리소스를 찾을 수 없습니다."),
  NOT_FRIEND(400, "F005", "친구 관계가 아닙니다."),

  /**
   *  Post Domain
   * */
  POST_NOT_FOUND(400, "P001", "해당 Post 리소스를 찾을 수 없습니다."),
  BOARD_NOT_FOUND(400, "B001", "해당 Board 리소스를 찾을 수 없습니다."),
  PHOTO_NOT_FOUND(400, "PH001", "해당 Photo 리소스를 찾을 수 없습니다."),
  POST_LIKE_NOT_FOUND(400, "PL001", "해당 User 와 Post 의 좋아요가 존재하지 않습니다."),
  POST_LIKE_ALREADY_EXISTS(400, "PL002", "해당 User 와 Post 의 좋아요가 이미 존재합니다."),

  /**
   * Timetable Domain
   */
  DUPLICATE_TIMETABLE_NAME(400, "T001", "이미 사용중인 이름입니다."),
  TIMETABLE_NOT_FOUND(404, "T002", "시간표를 찾을 수 없습니다."),
  LECTURE_NOT_FOUND(404, "T003", "강의를 찾을 수 없습니다."),
  ENROLLMENT_NOT_FOUND(404, "T004", "시간표에 강의가 등록되어 있지 않습니다."),
  INVALID_LECTURE_ADD_REQUEST(400, "T005", "시간표에 추가할 수 없는 강의입니다."),
  ALREADY_ADDED_LECTURE(400, "T006", "이미 추가된 강의입니다."),
  LECTURE_TIME_OVERLAP(400, "T007", "같은 시간에 다른 강의가 있습니다."),
  LECTURE_DETAIL_TIME_OVERLAP(400, "T008", "입력된 시간중 겹치는 시간이 있습니다."),

  /**
   * Comment Domain
   **/
  COMMENT_NOT_FOUND(400, "COM001", "해당 Comment를 찾을 수 없습니다."),
  COMMENT_LIKE_ALREADY_EXISTS(400, "COM002", "해당 댓글에 이미 좋아요가 존재합니다."),
  COMMENT_LIKE_NOT_FOUND(400, "COM003", "해당 댓글에 좋아요가 존재하지 않습니다.");
  
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
