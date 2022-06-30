package com.prgrms.coretime.timetable.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
public class OfficialLecturesResponse {
  private List<OfficialLectureInfo> content;
  private Pageable pageable;

  public OfficialLecturesResponse(
      List<OfficialLectureInfo> content, Pageable pageable) {
    this.content = content;
    this.pageable = pageable;
  }
}
