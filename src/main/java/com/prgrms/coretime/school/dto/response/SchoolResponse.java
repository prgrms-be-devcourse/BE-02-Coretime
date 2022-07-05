package com.prgrms.coretime.school.dto.response;

import com.prgrms.coretime.school.domain.School;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchoolResponse {

  private final Long schoolId;

  private final String name;

  private final String email;

  public static SchoolResponse from(School school) {
    return SchoolResponse.builder()
        .email(school.getEmail())
        .name(school.getName())
        .schoolId(school.getId())
        .build();
  }
}
