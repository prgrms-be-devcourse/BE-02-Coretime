package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.post.domain.Board;

public record BoardSimpleResponse(Long boardId, String name) {

  public BoardSimpleResponse(Board entity) {
    this(entity.getId(), entity.getName());
  }
}
