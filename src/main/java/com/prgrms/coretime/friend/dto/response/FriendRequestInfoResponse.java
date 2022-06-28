package com.prgrms.coretime.friend.dto.response;

import com.prgrms.coretime.friend.domain.Friend;
import lombok.Getter;

@Getter
public class FriendRequestInfoResponse {

  private final Long id;
  private final String nickname;

  public FriendRequestInfoResponse(Friend friend) {
    this.id = friend.getFollowerUser().getId();
    this.nickname = friend.getFollowerUser().getNickname();
  }
}
