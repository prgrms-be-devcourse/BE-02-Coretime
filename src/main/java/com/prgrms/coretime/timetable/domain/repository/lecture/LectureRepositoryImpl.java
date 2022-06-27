package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<OfficialLecture> findOfficialLectures(OfficialLectureSearchCondition officialLectureSearchCondition, Pageable pageable) {

    List<OfficialLecture> officialLectures = queryFactory
        .selectFrom(officialLecture)
        .where(
            searchCondition(officialLectureSearchCondition)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(officialLecture.count())
        .from(officialLecture)
        .where(searchCondition(officialLectureSearchCondition));

    return PageableExecutionUtils.getPage(officialLectures, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchCondition(OfficialLectureSearchCondition officialLectureSearchCondition) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(openYearEq(officialLectureSearchCondition.getOpenYear()));
    builder.and(semesterEq(officialLectureSearchCondition.getSemester()));

    return builder;
  }

  private BooleanExpression openYearEq(Integer openYear) {
    return openYear == null ? null : officialLecture.openYear.eq(openYear);
  }

  private BooleanExpression semesterEq(Semester semester) {
    return semester == null ? null : officialLecture.semester.eq(semester);
  }
}
