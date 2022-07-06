package com.prgrms.coretime.post.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

  @NotBlank
  private String title;
  private String content;
  @NotNull
  private Boolean isAnonymous;
  private List<MultipartFile> photos = new ArrayList<>();

  @Builder
  public PostCreateRequest(String title, String content, Boolean isAnonymous, List<MultipartFile> photos) {
    this.title = title;
    this.content = content;
    this.isAnonymous = isAnonymous;
    if (Objects.nonNull(photos)) {
      this.photos = photos;
    }
  }
}
