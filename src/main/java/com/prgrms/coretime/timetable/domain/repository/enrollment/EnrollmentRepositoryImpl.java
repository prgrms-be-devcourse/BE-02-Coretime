package com.prgrms.coretime.timetable.domain.repository.enrollment;

import static com.prgrms.coretime.timetable.domain.enrollment.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.lecture.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Enrollment> getEnrollmentsWithLectureByTimetableId(Long timetableId, LectureType lectureType) {
    BooleanBuilder condition = getEnrollmentWithLectureCondition(timetableId, lectureType);

    return queryFactory
        .select(enrollment)
        .from(enrollment)
        .join(enrollment.lecture, lecture)
        .fetchJoin()
        .where(condition)
        .fetch();
  }

  @Override
  public void deleteByTimetableId(Long timetableId) {
    queryFactory
        .delete(enrollment)
        .where(enrollment.enrollmentId.timeTableId.eq(timetableId))
        .execute();
  }

  private BooleanBuilder getEnrollmentWithLectureCondition(Long timetableId, LectureType lectureType) {
    BooleanBuilder enrollmentCondition = new BooleanBuilder();

    enrollmentCondition
        .and(enrollment.enrollmentId.timeTableId.eq(timetableId));

    if(lectureType.equals(CUSTOM)) {
      enrollmentCondition.and(enrollment.lecture.dType.eq("CUSTOM"));
    }

    return enrollmentCondition;
  }
}
