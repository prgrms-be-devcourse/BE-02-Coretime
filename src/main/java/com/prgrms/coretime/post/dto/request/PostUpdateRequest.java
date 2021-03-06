package com.prgrms.coretime.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

  @NotBlank
  private String title;
  private String content;

  public PostUpdateRequest(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
