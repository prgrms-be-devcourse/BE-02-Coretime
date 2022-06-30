package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

import com.prgrms.coretime.timetable.domain.lecture.CustomLecture;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LectureDetailRepositoryTest {
  @Autowired
  private LectureDetailRepository lectureDetailRepository;

  @Autowired
  private EntityManager em;

  private CustomLecture lectureA, lectureB;
}