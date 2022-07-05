package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.post.domain.Photo;

public record PhotoResponse(Long photo_id, String path) {

  public PhotoResponse(Photo entity) {
    this(
        entity.getId(),
        entity.getPath()
    );
  }
}
