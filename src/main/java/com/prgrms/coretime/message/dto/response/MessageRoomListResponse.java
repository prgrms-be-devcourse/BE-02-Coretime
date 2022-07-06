package com.prgrms.coretime.message.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageRoomListResponse {
  private final Long messageRoomId;
  private final Boolean isAnonymous;
  private final String interlocutorNickname;
  private final LocalDateTime lastMessageSentTime;
  private final String lastMessageContent;

  @Builder
  public MessageRoomListResponse(Long messageRoomId, Boolean isAnonymous,
      String interlocutorNickname, LocalDateTime lastMessageSentTime,
      String lastMessageContent) {
    this.messageRoomId = messageRoomId;
    this.isAnonymous = isAnonymous;
    this.interlocutorNickname = interlocutorNickname;
    this.lastMessageSentTime = lastMessageSentTime;
    this.lastMessageContent = lastMessageContent;
  }
}
