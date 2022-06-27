package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.timetable.service.LectureService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"lectures"})
@RestController
@RequestMapping("/api/v1/lectures")
@AllArgsConstructor
public class LectureController {
  private final LectureService lectureService;
}
