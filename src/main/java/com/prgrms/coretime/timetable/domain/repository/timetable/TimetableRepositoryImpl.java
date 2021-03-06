package com.prgrms.coretime.timetable.domain.repository.timetable;

import static com.prgrms.coretime.timetable.domain.QTimetable.timetable;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.Timetable;
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
    BooleanBuilder sameTableCondition = getSameTableCondition(userId, name, year, semester);

    return Optional.ofNullable(
        queryFactory
            .selectFrom(timetable)
            .where(sameTableCondition)
            .fetchOne()
    );
  }

  @Override
  public Optional<Timetable> getDefaultTimetable(Long userId, Integer year, Semester semester) {
    BooleanBuilder defaultTimetableCondition = getDefaultTimetableCondition(userId, year, semester);

    return Optional.ofNullable(queryFactory
        .selectFrom(timetable)
        .where(defaultTimetableCondition)
        .fetchOne()
    );
  }

  @Override
  public Optional<Timetable> getTimetableByUserIdAndTimetableId(Long userId, Long timetableId) {
    BooleanBuilder timetableCondition = getTimetableCondition(userId, timetableId);

    return Optional.ofNullable(queryFactory
        .selectFrom(timetable)
        .where(timetableCondition)
        .fetchOne());
  }

  @Override
  public Optional<Timetable> getRecentlyAddedTimetable(Long userId, Integer year,
      Semester semester) {
    BooleanBuilder idYearSemesterCondition = getIdYearSemesterCondition(userId, year, semester);

    return Optional.ofNullable(
        queryFactory
            .selectFrom(timetable)
            .where(idYearSemesterCondition)
            .orderBy(timetable.createdAt.desc())
            .limit(1)
            .fetchOne()
    );
  }

  @Override
  public List<Timetable> getDefaultTimetables(Long userId) {
    BooleanBuilder defaultTimetablesCondition = getDefaultTimetablesCondition(userId);

    return queryFactory
        .selectFrom(timetable)
        .where(defaultTimetablesCondition)
        .fetch();
  }

  @Override
  public List<Timetable> getTimetables(Long userId, Integer year, Semester semester) {
    BooleanBuilder idYearSemesterCondition = getIdYearSemesterCondition(userId, year, semester);

    return queryFactory
        .selectFrom(timetable)
        .where(idYearSemesterCondition)
        .orderBy(timetable.name.asc())
        .fetch();
  }

  @Override
  public boolean isFirstTable(Long userId, Integer year, Semester semester) {
    BooleanBuilder countOfTableCondition = getCountOfTableCondition(userId, year, semester);

    long countOfTimetable = queryFactory
        .select(timetable.count())
        .from(timetable)
        .where(countOfTableCondition)
        .fetchOne();

    return countOfTimetable == 0 ? true : false;
  }

  @Override
  public void deleteByTimetableId(Long timetableId) {
    queryFactory
        .delete(timetable)
        .where(timetableIdEq(timetableId))
        .execute();
  }

  private BooleanBuilder getSameTableCondition(Long userId, String name, Integer year, Semester semester) {
    BooleanBuilder sameNameTableCondition = new BooleanBuilder();

    sameNameTableCondition
        .and(timetableUserIdEq(userId))
        .and(timetableYearEq(year))
        .and(timetableSemesterEq(semester))
        .and(timetableNameEq(name));

    return sameNameTableCondition;
  }

  private BooleanBuilder getDefaultTimetableCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder defaultTableCondition = new BooleanBuilder();

    defaultTableCondition
        .and(timetableUserIdEq(userId))
        .and(timetableYearEq(year))
        .and(timetableSemesterEq(semester))
        .and(timetable.isDefault.eq(true));

    return defaultTableCondition;
  }

  private BooleanBuilder getTimetableCondition(Long userId, Long timetableId) {
    BooleanBuilder timetableCondition = new BooleanBuilder();

    timetableCondition
        .and(timetableUserIdEq(userId))
        .and(timetableIdEq(timetableId));

    return timetableCondition;
  }

  private BooleanBuilder getDefaultTimetablesCondition(Long userId) {
    BooleanBuilder defaultTimetablesCondition = new BooleanBuilder();

    defaultTimetablesCondition
        .and(timetableUserIdEq(userId))
        .and(timetable.isDefault.eq(true));

    return defaultTimetablesCondition;
  }

  private BooleanBuilder getIdYearSemesterCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder idYearSemesterCondition = new BooleanBuilder();

    idYearSemesterCondition
        .and(timetableUserIdEq(userId))
        .and(timetableYearEq(year))
        .and(timetableSemesterEq(semester));

    return idYearSemesterCondition;
  }

  private BooleanBuilder getCountOfTableCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder countOfTableCondition = new BooleanBuilder();

    countOfTableCondition
        .and(timetableUserIdEq(userId))
        .and(timetableYearEq(year))
        .and(timetableSemesterEq(semester));

    return countOfTableCondition;
  }

  private BooleanExpression timetableUserIdEq(Long userId) {
    return userId == null ? null : timetable.user.id.eq(userId);
  }

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : timetable.id.eq(timetableId);
  }

  private BooleanExpression timetableYearEq(Integer year) {
    return year == null ? null : timetable.year.eq(year);
  }

  private BooleanExpression timetableSemesterEq(Semester semester) {
    return semester == null ? null : timetable.semester.eq(semester);
  }

  private BooleanExpression timetableNameEq(String name) {
    return hasText(name) ? timetable.name.eq(name) : null;
  }
}
