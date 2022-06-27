package com.prgrms.coretime.timetable.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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

}