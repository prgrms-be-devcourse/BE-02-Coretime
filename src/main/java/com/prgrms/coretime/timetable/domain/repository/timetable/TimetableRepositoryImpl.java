package com.prgrms.coretime.timetable.domain.repository.timetable;

import static com.prgrms.coretime.timetable.domain.timetable.QTimetable.timetable;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimetableRepositoryImpl implements TimetableCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Timetable> getTimetableBySameName(Long userId, String name, Integer year, Semester semester) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(timetable)
            .where(
                getSameTableCondition(userId, name, year, semester)
            )
            .fetchOne()
    );
  }

  @Override
  public List<Timetable> getTimetables(Long userId, Integer year, Semester semester) {
    return queryFactory
        .selectFrom(timetable)
        .where(
            getTimetablesCondition(userId, year, semester)
        )
        .orderBy(timetable.name.asc())
        .fetch();
  }

  @Override
  public Optional<Timetable> getTimetableByUserIdAndTimetableId(Long userId, Long timetableId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(timetable)
        .where(
            getTimetableCondition(userId, timetableId)
        )
        .fetchOne());
  }

  @Override
  public Optional<Timetable> getDefaultTimetable(Long userId, Integer year, Semester semester) {
    return Optional.ofNullable(queryFactory
        .selectFrom(timetable)
        .where(
            getDefaultTimetableCondition(userId, year, semester)
        )
        .fetchOne()
    );
  }

  @Override
  public boolean isFirstTimetable(Long userId, Integer year, Semester semester) {
    long countOfTimetable = queryFactory
        .select(timetable.count())
        .from(timetable)
        .where(
            getCountOfTableCondition(userId, year, semester)
        )
        .fetchOne();

    return countOfTimetable == 0 ? true : false;
  }

  @Override
  public void deleteByTimetableId(Long timetableId) {
    queryFactory
        .delete(timetable)
        .where(timetable.id.eq(timetableId))
        .execute();
  }

  private BooleanBuilder getSameTableCondition(Long userId, String name, Integer year, Semester semester) {
    BooleanBuilder sameNameTableCondition = new BooleanBuilder();

    sameNameTableCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester))
        .and(nameEq(name));

    return sameNameTableCondition;
  }

  private BooleanBuilder getTimetablesCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder timetablesCondition = new BooleanBuilder();

    timetablesCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester));

    return timetablesCondition;
  }

  private BooleanBuilder getTimetableCondition(Long userId, Long timetableId) {
    BooleanBuilder timetableCondition = new BooleanBuilder();

    timetableCondition
        .and(userIdEq(userId))
        .and(timetableIdEq(timetableId));

    return timetableCondition;
  }

  private BooleanBuilder getDefaultTimetableCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder defaultTableCondition = new BooleanBuilder();

    defaultTableCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester))
        .and(timetable.isDefault.eq(true));

    return defaultTableCondition;
  }

  private BooleanBuilder getCountOfTableCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder countOfTableCondition = new BooleanBuilder();

    countOfTableCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester));

    return countOfTableCondition;
  }

  private BooleanExpression userIdEq(Long userId) {
    return userId == null ? null : timetable.user.id.eq(userId);
  }

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : timetable.id.eq(timetableId);
  }

  private BooleanExpression yearEq(Integer year) {
    return year == null ? null : timetable.year.eq(year);
  }

  private BooleanExpression semesterEq(Semester semester) {
    return semester == null ? null : timetable.semester.eq(semester);
  }

  private BooleanExpression nameEq(String name) {
    return hasText(name) ? timetable.name.eq(name) : null;
  }
}
