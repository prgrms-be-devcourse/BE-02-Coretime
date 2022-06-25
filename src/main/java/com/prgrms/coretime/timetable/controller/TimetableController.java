package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.TimetableResponse;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.timetable.service.TimetableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    Long createTimetableId = timetableService.createTimetable(timetableCreateRequest);

    ApiResponse apiResponse = new ApiResponse("시간표 생성 완료");

    return ResponseEntity
        .created(URI.create("/timetables/" + createTimetableId))
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표 목록 조회", notes = "연도와 학기에 따른 시간표 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<TimetablesResponse>> getTimetables(@RequestParam Integer year, @RequestParam Semester semester) {
    TimetablesResponse timetablesResponse = timetableService.getTimetables(year, semester);

    ApiResponse apiResponse = new ApiResponse("시간표 목록 조회 완료", timetablesResponse);

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표 조회", notes = "전달된 timetableId에 따라 시간표를 전달합니다.")
  @GetMapping("/{timetableId}")
  public ResponseEntity<ApiResponse<TimetableResponse>> getTimetable(@PathVariable Long timetableId) {
    TimetableResponse timetableResponse = timetableService.getTimetable(timetableId);

    ApiResponse apiResponse = new ApiResponse("시간표 조회 완료", timetableResponse);

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표 이름 변경", notes = "시간표의 이름을 변경합니다.")
  @PatchMapping("/{timetableId}")
  public ResponseEntity<ApiResponse> updateTimetableName(@PathVariable Long timetableId, @RequestBody @Valid TimetableUpdateRequest timetableUpdateRequest) {
    timetableService.updateTimetableName(timetableId, timetableUpdateRequest);

    ApiResponse apiResponse = new ApiResponse("시간표 이름 수정 완료");

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표 삭제", notes = "전달된 timetableId에 따라 시간표를 삭제합니다.")
  @DeleteMapping("/{timetableId}")
  public ResponseEntity<ApiResponse> deleteTimetable(@PathVariable Long timetableId) {
    timetableService.deleteTimetable(timetableId);

    ApiResponse apiResponse = new ApiResponse("시간표 삭제 완료");

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
