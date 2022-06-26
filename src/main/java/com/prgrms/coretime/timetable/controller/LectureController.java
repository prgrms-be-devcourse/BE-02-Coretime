package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.timetable.dto.request.OfficialLecturesCreateRequest;
import com.prgrms.coretime.timetable.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"lectures"})
@RestController
@RequestMapping("/api/v1/lectures")
@AllArgsConstructor
public class LectureController {
  private final LectureService lectureService;

  @ApiOperation(value = "official 강의 추가", notes = "official 강의들을 추가합니다.")
  @PostMapping("/officials")
  public ResponseEntity<ApiResponse> addOfficialLectures(@RequestBody @Valid OfficialLecturesCreateRequest officialLecturesCreateRequest) {
    lectureService.addOfficialLectures(officialLecturesCreateRequest);

    ApiResponse apiResponse = new ApiResponse("official 강의 추가 완료");

    return ResponseEntity
        .created(null)
        .body(apiResponse);
  }

}
