package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.service.TimetableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"timetables"})
@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
  private final TimetableService timetableService;

  @ApiOperation(value = "시간표 생성", notes = "시간표를 생성합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse> createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    timetableService.createTimetable(timetableCreateRequest);

    ApiResponse apiResponse = new ApiResponse("시간표 생성 완료", null);

    return ResponseEntity
        .created(null)
        .body(apiResponse);
  }


  @GetMapping
  public ResponseEntity<ApiResponse> getTimetable() {
    ApiResponse apiResponse = new ApiResponse("????", null);

    return ResponseEntity
        .created(null)
        .body(apiResponse);
  }
}
