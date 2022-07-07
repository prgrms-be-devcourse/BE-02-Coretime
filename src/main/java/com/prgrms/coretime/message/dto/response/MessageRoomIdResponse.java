package com.prgrms.coretime.message.dto.response;

import com.prgrms.coretime.message.domain.MessageRoom;
import lombok.Getter;

@Getter
public class MessageRoomIdResponse {
  private final Long messageRoomId;

  public MessageRoomIdResponse(MessageRoom messageRoom) {
    this.messageRoomId = messageRoom.getId();
  }
}
