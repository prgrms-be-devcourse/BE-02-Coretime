package com.prgrms.coretime.timetable.domain.repository.enrollment;

import static com.prgrms.coretime.timetable.domain.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;

import com.prgrms.coretime.timetable.domain.Enrollment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;

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
  @Modifying(clearAutomatically = true)
  public void deleteByTimetableId(Long timetableId) {
    queryFactory
        .delete(enrollment)
        .where(timetableIdEq(timetableId))
        .execute();
  }

  private BooleanBuilder getEnrollmentWithLectureCondition(Long timetableId, LectureType lectureType) {
    BooleanBuilder enrollmentCondition = new BooleanBuilder();

    enrollmentCondition.and(timetableIdEq(timetableId));

    if(lectureType.equals(CUSTOM)) {
      enrollmentCondition.and(enrollment.lecture.dType.eq("CUSTOM"));
    }

    return enrollmentCondition;
  }

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }
}
