package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.timetable.dto.response.OfficialLecturesResponse;
import com.prgrms.coretime.timetable.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"lectures"})
@RestController
@RequestMapping("/api/v1/lectures")
@AllArgsConstructor
public class LectureController {
  private final LectureService lectureService;

  @ApiOperation(value = "강의 목록 조회", notes = "강의 목록을 조회합니다.")
  @GetMapping("/officials")
  public ResponseEntity<ApiResponse> getOfficialLectures(Pageable pageable) {
    OfficialLecturesResponse officialLecturesResponse = lectureService.getOfficialLectures(pageable);

    ApiResponse apiResponse = new ApiResponse("강의 목록 조회 완료", officialLecturesResponse);

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
