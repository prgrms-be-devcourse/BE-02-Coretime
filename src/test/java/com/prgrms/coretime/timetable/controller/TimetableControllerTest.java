package com.prgrms.coretime.timetable.controller;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.service.TimetableService;
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
@WebMvcTest(TimetableController.class)
class TimetableControllerTest {
  @MockBean
  TimetableService timetableService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  @DisplayName("시간표 생성 테스트")
  class TimetableCreateTest {
    @ParameterizedTest(name = "{index}")
    @MethodSource("timetableCreatRequestParameter")
    @DisplayName("request dto의 validation이 잘 되는지 테스트")
    void testTimetableCreateRequestValidation(String name, Integer year, Semester semester) throws Exception {
      TimetableCreateRequest timetableCreateRequest = TimetableCreateRequest.builder()
          .name(name)
          .year(year)
          .semester(semester)
          .build();

      mockMvc.perform(post("/api/v1/timetables")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(timetableCreateRequest)))
          .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> timetableCreatRequestParameter() {
      return Stream.of(
          Arguments.of(null, 2022, SECOND),
          Arguments.of("시간표1", null, SECOND),
          Arguments.of("시간표1", 2022, null)
      );
    }

    @Test
    @DisplayName("요청을 정상적으로 처리한경우 status가 201인지 확인하는 테스트")
    void testTimetableCreate() throws Exception {
      TimetableCreateRequest timetableCreateRequest = TimetableCreateRequest.builder()
          .name("시간표1")
          .year(2022)
          .semester(SECOND)
          .build();

      mockMvc.perform(post("/api/v1/timetables")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(timetableCreateRequest)))
          .andExpect(status().isCreated());
    }
  }
}