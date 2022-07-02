package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import com.prgrms.coretime.timetable.dto.response.OfficialLectureInfo;
import com.prgrms.coretime.timetable.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"lectures"})
@RestController
@RequestMapping("/api/v1/lectures")
@AllArgsConstructor
public class LectureController {
  private final LectureService lectureService;

  @ApiOperation(value = "강의 목록 조회", notes = "강의 목록을 조회합니다.")
  @GetMapping("/officials")
  public ResponseEntity<ApiResponse<Page<OfficialLectureInfo>>> getOfficialLectures(
      @AuthenticationPrincipal JwtPrincipal principal, @Valid OfficialLectureSearchRequest officialLectureSearchRequest, Pageable pageable) {
    return ResponseEntity
        .ok()
        .body(
            new ApiResponse(
                "강의 목록 조회 완료",
                lectureService.getOfficialLectures(principal.schoolId, officialLectureSearchRequest, pageable)
            )
        );
  }
}
