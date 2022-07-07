package com.prgrms.coretime.message.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageRoomGetRequest {

  @NotNull(message = "쪽지방 id는 필수값입니다.")
  private Long messageRoomId;

}
