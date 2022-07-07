package com.prgrms.coretime.message.dto.response;

import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.User;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MessageResponse {

  private final Boolean isReceived;
  private final LocalDateTime createdAt;
  private final String content;

  public MessageResponse(Message message, User interlocutor) {
    this.isReceived = interlocutor.getId() == message.getWriter().getId() ? true : false;
    this.createdAt = message.getCreatedAt();
    this.content = message.getContent();
  }
}
