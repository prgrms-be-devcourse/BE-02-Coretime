package com.prgrms.coretime.school.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.school.dto.response.SchoolResponse;
import com.prgrms.coretime.school.service.SchoolService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/schools")
public class SchoolController {

  private final SchoolService schoolService;

  public SchoolController(SchoolService schoolService) {
    this.schoolService = schoolService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<SchoolResponse>>> getSchools() {
    List<SchoolResponse> schoolList = schoolService.findAll().stream().map(SchoolResponse::from).toList();
    return ResponseEntity.ok(new ApiResponse<>("학교 조회 성공하였습니다.", schoolList));
  }
}
