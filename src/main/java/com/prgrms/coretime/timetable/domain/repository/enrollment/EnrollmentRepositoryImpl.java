package com.prgrms.coretime.timetable.domain.repository.enrollment;

import static com.prgrms.coretime.timetable.domain.enrollment.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.lecture.QLecture.lecture;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Enrollment> getEnrollmentByIdWithLecture(Long timetableId) {

    return queryFactory
        .select(enrollment)
        .from(enrollment)
        .join(enrollment.lecture, lecture)
        .fetchJoin()
        .where(
            getEnrollmentCondition(timetableId)
        )
        .fetch();
  }

  private BooleanBuilder getEnrollmentCondition(Long timetableId) {
    BooleanBuilder enrollmentCondition = new BooleanBuilder();

    enrollmentCondition
        .and(timetableIdEq(timetableId));

    return enrollmentCondition;
  }

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }
}
