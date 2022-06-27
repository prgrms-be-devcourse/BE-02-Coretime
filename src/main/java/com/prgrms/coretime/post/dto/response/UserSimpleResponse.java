package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.user.domain.User;

public record UserSimpleResponse(Long userId, String nickname) {

  public UserSimpleResponse(User entity) {
    this(entity.getId(), entity.getNickname());
  }
}
