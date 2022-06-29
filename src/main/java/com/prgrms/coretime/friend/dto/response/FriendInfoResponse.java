package com.prgrms.coretime.friend.dto.response;

import com.prgrms.coretime.friend.domain.Friend;
import lombok.Getter;

@Getter
public class FriendInfoResponse {

  private final Long id;
  private final String nickname;

  public FriendInfoResponse(Friend friend) {
    this.id = friend.getFolloweeUser().getId();
    this.nickname = friend.getFolloweeUser().getNickname();
  }
}
