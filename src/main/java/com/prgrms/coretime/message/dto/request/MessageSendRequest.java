package com.prgrms.coretime.message.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageSendRequest {

  @NotBlank(message = "전송할 쪽지 내용은 필수값입니다.")
  private String message;
}
