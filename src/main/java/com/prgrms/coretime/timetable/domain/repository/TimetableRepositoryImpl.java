package com.prgrms.coretime.timetable.domain.repository;

import static com.prgrms.coretime.timetable.domain.timetable.QTimetable.timetable;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Semester;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimetableRepositoryImpl implements TimetableCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public long countDuplicateTimetableName(String name, Integer year, Semester semester) {

    return queryFactory
        .select(timetable.count())
        .from(timetable)
        .where(
            yearEq(year),
            semesterEq(semester),
            nameEq(name)
        )
        .fetchOne();
  }

  private BooleanExpression yearEq(Integer year) {
    return year != null ? timetable.year.eq(year) : null;
  }

  private BooleanExpression semesterEq(Semester semester) {
    return semester != null ? timetable.semester.eq(semester) : null;
  }

  private BooleanExpression nameEq(String name) {
    return hasText(name) ? timetable.name.eq(name) : null;
  }
}
