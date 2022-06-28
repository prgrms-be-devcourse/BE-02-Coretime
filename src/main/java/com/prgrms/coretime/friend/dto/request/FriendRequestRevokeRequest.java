package com.prgrms.coretime.friend.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FriendRequestRevokeRequest {

  @NotNull
  private Long followeeId;
}
