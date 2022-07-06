package com.prgrms.coretime.timetable.domain.repository.enrollment;

import static com.prgrms.coretime.timetable.domain.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;

import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.util.FlushAndClear;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Enrollment> getEnrollmentsWithLectureByTimetableId(Long timetableId, LectureType lectureType) {
    BooleanBuilder enrollmentWithLectureCondition = getEnrollmentWithLectureCondition(timetableId, lectureType);

    return queryFactory
        .select(enrollment)
        .from(enrollment)
        .join(enrollment.lecture, lecture)
        .fetchJoin()
        .where(enrollmentWithLectureCondition)
        .fetch();
  }

  @Override
  @FlushAndClear
  public void deleteByTimetableId(Long timetableId) {
    queryFactory
        .delete(enrollment)
        .where(enrollmentTimetableIdEq(timetableId))
        .execute();
  }

  private BooleanBuilder getEnrollmentWithLectureCondition(Long timetableId, LectureType lectureType) {
    BooleanBuilder enrollmentCondition = new BooleanBuilder();

    enrollmentCondition.and(enrollmentTimetableIdEq(timetableId));

    if(lectureType.equals(CUSTOM)) {
      enrollmentCondition.and(enrollment.lecture.dType.eq("CUSTOM"));
    }

    return enrollmentCondition;
  }

  private BooleanExpression enrollmentTimetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }
}
