package com.prgrms.coretime.message.dto.response;

import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.user.domain.TestUser;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class MessageRoomResponse {

  private final String boardName;
  private final String postTitle;
  private final String interlocutorNickname;
  private final Boolean isBlocked;
  private final Page<MessageResponse> messages;

  @Builder
  public MessageRoomResponse(MessageRoom messageRoom, Page<Message> messages, TestUser interlocutor) {
    this.boardName = messageRoom.getCreatedFrom().getBoard().getName();
    this.postTitle = messageRoom.getCreatedFrom().getTitle();
    this.isBlocked = messageRoom.getIsBlocked();
    this.interlocutorNickname = messageRoom.getIsAnonymous() ? "익명" : interlocutor.getNickname();
    this.messages = messages.map(message -> new MessageResponse(message, interlocutor));
  }
}
