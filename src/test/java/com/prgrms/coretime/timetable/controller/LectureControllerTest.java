package com.prgrms.coretime.timetable.controller;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.lecture.LectureType.MAJOR;
import static com.prgrms.coretime.timetable.domain.lectureDetail.Day.MON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.timetable.dto.request.LectureDetailRequest;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureRequest;
import com.prgrms.coretime.timetable.dto.request.OfficialLecturesCreateRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(LectureController.class)
class LectureControllerTest {
  @MockBean
  LectureController lectureController;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  class OfficialLectureCreateTest {
    @Test
    @DisplayName("OfficialLecturesCreateRequest 필드 validation 테스트")
    void testOfficialLectureCreateRequestValidation() throws Exception{
      OfficialLecturesCreateRequest officialLecturesCreateRequest = new OfficialLecturesCreateRequest(null);

      mockMvc.perform(post("/api/v1/lectures/officials")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(officialLecturesCreateRequest)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("OfficialLectureRequest 필드 validation 테스트")
    void testOfficialLectureRequestValidation() throws Exception{
      OfficialLectureRequest officialLectureRequest = OfficialLectureRequest.builder()
          .name("과목")
          .professor("김")
          .classRoom("101")
          .semester(SECOND)
          .year(2022)
          .credit(3.0)
          .code("483190")
          .lectureType(MAJOR)
          .lectureDetails(null)
          .build();

      List<OfficialLectureRequest> lectures = new ArrayList();
      lectures.add(officialLectureRequest);

      OfficialLecturesCreateRequest officialLecturesCreateRequest = new OfficialLecturesCreateRequest(lectures);

      mockMvc.perform(post("/api/v1/lectures/officials")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(officialLecturesCreateRequest)))
          .andExpect(status().isBadRequest());
    }


    @ParameterizedTest(name = "{0} ~ {1}")
    @MethodSource("lectureDetailRequestParameter")
    @DisplayName("OfficialLectureRequest 필드 validation 테스트")
    void
    testLectureDetailRequestValidation(String startTime, String endTime) throws Exception{
      LectureDetailRequest lectureDetailRequest = LectureDetailRequest.builder()
          .day(MON)
          .startTime(startTime)
          .endTime(endTime)
          .build();
      List<LectureDetailRequest> lectureDetails = new ArrayList<>();
      lectureDetails.add(lectureDetailRequest);

      OfficialLectureRequest officialLectureRequest = OfficialLectureRequest.builder()
          .name("과목")
          .professor("김")
          .classRoom("101")
          .semester(SECOND)
          .year(2022)
          .credit(3.0)
          .code("483190")
          .lectureType(MAJOR)
          .lectureDetails(lectureDetails)
          .build();

      List<OfficialLectureRequest> lectures = new ArrayList();
      lectures.add(officialLectureRequest);
      OfficialLecturesCreateRequest officialLecturesCreateRequest = new OfficialLecturesCreateRequest(lectures);

      mockMvc.perform(post("/api/v1/lectures/officials")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(officialLecturesCreateRequest)))
          .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> lectureDetailRequestParameter() {
      return Stream.of(
          Arguments.of("9:11", "11:10"),
          Arguments.of("9:11", "9:50"),
          Arguments.of("10:11", "11:10"),
          Arguments.of("10:00", "11:11")
      );
    }

    @Test
    @DisplayName("official 강의 생성 성공 테스트 ")
    void testOfficialLecturesCreate() throws Exception{
      LectureDetailRequest lectureDetailRequest = LectureDetailRequest.builder()
          .day(MON)
          .startTime("10:00")
          .endTime("11:50")
          .build();
      List<LectureDetailRequest> lectureDetails = new ArrayList<>();
      lectureDetails.add(lectureDetailRequest);

      OfficialLectureRequest officialLectureRequest = OfficialLectureRequest.builder()
          .name("과목")
          .professor("김")
          .classRoom("101")
          .semester(SECOND)
          .year(2022)
          .credit(3.0)
          .code("483190")
          .lectureType(MAJOR)
          .lectureDetails(lectureDetails)
          .build();

      List<OfficialLectureRequest> lectures = new ArrayList();
      lectures.add(officialLectureRequest);
      OfficialLecturesCreateRequest officialLecturesCreateRequest = new OfficialLecturesCreateRequest(lectures);

      mockMvc.perform(post("/api/v1/lectures/officials")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(officialLecturesCreateRequest)))
          .andExpect(status().isOk());
    }
  }
}