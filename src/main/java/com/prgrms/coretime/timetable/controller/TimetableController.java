package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.dto.request.CustomLectureRequest;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.TimetableResponse;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.timetable.service.EnrollmentService;
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
import org.springframework.web.bind.annotation.PutMapping;
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
  private final EnrollmentService enrollmentService;

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

  // 기본 시간표 조회
  @ApiOperation(value = "기본 시간표 조회", notes = "연도와 학기에 해당하는 사용자의 기본 시간표를 조회합니다.")
  @GetMapping("/default")
  public ResponseEntity<ApiResponse<TimetableResponse>> getDefaultTimetable(@RequestParam Integer year, @RequestParam Semester semester) {
    TimetableResponse timetableResponse = timetableService.getDefaultTimetable(year, semester);

    ApiResponse apiResponse = new ApiResponse("기본 시간표 조회 완료", timetableResponse);

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

  @ApiOperation(value = "시간표 정보 변경", notes = "시간표의 이름과 기본 시간표 여부를 변경합니다.")
  @PatchMapping("/{timetableId}")
  public ResponseEntity<ApiResponse> updateTimetableName(@PathVariable Long timetableId, @RequestBody @Valid TimetableUpdateRequest timetableUpdateRequest) {
    timetableService.updateTimetable(timetableId, timetableUpdateRequest);

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

  @ApiOperation(value = "시간표에 official 강의 추가", notes = "시간표에 official 강의를 추가합니다.")
  @PostMapping("/{timetableId}/enrollments")
  public ResponseEntity<ApiResponse> addOfficialLectureToTimetable(@PathVariable Long timetableId, @RequestBody @Valid
      EnrollmentCreateRequest enrollmentCreateRequest) {
     Enrollment enrollment = enrollmentService.addOfficialLectureToTimetable(timetableId, enrollmentCreateRequest);

    ApiResponse apiResponse = new ApiResponse("official 강의 시간표에 추가 완료");

    return ResponseEntity
        .created(URI.create(String.format("/timetables/%s/enrollments/%s", timetableId, enrollment.getEnrollmentId().getLectureId())))
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표에 custom 강의 추가", notes = "시간표에 custom 강의를 추가합니다.")
  @PostMapping("/{timetableId}/enrollments/custom-lectures")
  public ResponseEntity<ApiResponse> addCustomLectureToTimetable(@PathVariable Long timetableId, @RequestBody @Valid
      CustomLectureRequest customLectureCreateRequest) {
    Enrollment enrollment = enrollmentService.addCustomLectureToTimetable(timetableId, customLectureCreateRequest);

    ApiResponse apiResponse = new ApiResponse("custom 강의 시간표에 추가 완료");

    return ResponseEntity
        .created(URI.create(String.format("/timetables/%s/enrollments/custom-lectures/%s", timetableId, enrollment.getEnrollmentId().getLectureId())))
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표에 추가된 custom 강의 수정", notes = "시간표에 추가된 custom 강의를 수정합니다.")
  @PutMapping("/{timetableId}/enrollments/custom-lectures/{lectureId}")
  public ResponseEntity<ApiResponse> addCustomLectureToTimetable(@PathVariable Long timetableId, @PathVariable Long lectureId, @RequestBody @Valid
      CustomLectureRequest customLectureCreateRequest) {
    enrollmentService.updateCustomLecture(timetableId, lectureId, customLectureCreateRequest);

    ApiResponse apiResponse = new ApiResponse("시간표에 추가된 custom 강의 수정 완료");

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표에서 강의 삭제", notes = "강의를 시간표에서 삭제합니다")
  @DeleteMapping("/{timetableId}/enrollments/{lectureId}")
  public ResponseEntity<ApiResponse> deleteLectureFromTimetable(@PathVariable Long timetableId, @PathVariable Long lectureId) {
    enrollmentService.deleteLectureFromTimetable(timetableId, lectureId);

    ApiResponse apiResponse = new ApiResponse("시간표에서 강의 삭제 완료");

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
