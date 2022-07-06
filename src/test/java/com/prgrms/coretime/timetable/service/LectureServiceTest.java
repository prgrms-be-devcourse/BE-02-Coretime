package com.prgrms.coretime.timetable.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {
  @Mock
  private LectureRepository lectureRepository;
  @Mock
  private LectureValidator lectureValidator;
  @InjectMocks
  private LectureService lectureService;

  private Long schoolId;
  private OfficialLectureSearchRequest officialLectureSearchRequest = OfficialLectureSearchRequest.builder()
      .build();
  private PageRequest pageRequest = PageRequest.of(0, 20);


  @Test
  @DisplayName("dto 검증 실패한 경우 테스트")
  void testValidationFail() {
    doThrow(new IllegalArgumentException()).when(lectureValidator).validateOfficialLectureSearchRequest(schoolId, officialLectureSearchRequest);

    try {
      lectureService.getOfficialLectures(schoolId, officialLectureSearchRequest, pageRequest);
    }catch (IllegalArgumentException e) {
      verify(lectureRepository, never()).getOfficialLectures(any(), any());
    }
  }

  @Test
  @DisplayName("dto 검증 성공하여 ")
  void testGetOfficialLectures() {
    Page<OfficialLecture> officialLectures = new PageImpl<>(new ArrayList<OfficialLecture>());
    when(lectureRepository.getOfficialLectures(any(), any())).thenReturn(officialLectures);

    lectureService.getOfficialLectures(schoolId, officialLectureSearchRequest, pageRequest);

    verify(lectureValidator).validateOfficialLectureSearchRequest(schoolId, officialLectureSearchRequest);
    verify(lectureRepository).getOfficialLectures(any(), any());
  }
}