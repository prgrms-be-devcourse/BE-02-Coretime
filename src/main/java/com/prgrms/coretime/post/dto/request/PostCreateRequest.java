package com.prgrms.coretime.post.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

  @NotBlank
  private String title;
  private String content;
  @NotNull
  private Boolean isAnonymous;

  @Builder
  public PostCreateRequest(String title, String content, Boolean isAnonymous) {
    this.title = title;
    this.content = content;
    this.isAnonymous = isAnonymous;
  }
}
