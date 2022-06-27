package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
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
        .where(
            searchCondition(officialLectureSearchCondition)
        );

    return PageableExecutionUtils.getPage(officialLectures, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchCondition(OfficialLectureSearchCondition officialLectureSearchCondition) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(openYearEq(officialLectureSearchCondition.getOpenYear()));
    builder.and(semesterEq(officialLectureSearchCondition.getSemester()));

    if(officialLectureSearchCondition.getLectureTypes() != null) {
      BooleanBuilder lectureTypeBuilder = new BooleanBuilder();
      for(LectureType lectureType : officialLectureSearchCondition.getLectureTypes()) {
        lectureTypeBuilder.or(lectureTypeEq(lectureType));
      }
      builder.and(lectureTypeBuilder);
    }

    if(officialLectureSearchCondition.getGrades() != null) {
      BooleanBuilder gradeBuilder = new BooleanBuilder();
      gradeBuilder.or(gradeEq(Grade.ETC));
      for(Grade grade : officialLectureSearchCondition.getGrades()) {
        gradeBuilder.or(gradeEq(grade));
      }
      builder.and(gradeBuilder);
    }

    if(officialLectureSearchCondition.getCredits() != null) {
      BooleanBuilder creditBuilder = new BooleanBuilder();
      for(Double credit : officialLectureSearchCondition.getCredits()) {
        if(credit >= 4.0) {
          creditBuilder.or(creditGoe(credit));
        }else{
          creditBuilder.or(creditEq(credit));
        }
      }
      builder.and(creditBuilder);
    }

    return builder;
  }

  private BooleanExpression openYearEq(Integer openYear) {
    return openYear == null ? null : officialLecture.openYear.eq(openYear);
  }

  private BooleanExpression semesterEq(Semester semester) {
    return semester == null ? null : officialLecture.semester.eq(semester);
  }

  private BooleanExpression lectureTypeEq(LectureType lectureType) {
    return lectureType == null ? null : officialLecture.lectureType.eq(lectureType);
  }

  private BooleanExpression gradeEq(Grade grade) {
    return grade == null ? null : officialLecture.grade.eq(grade);
  }

  private BooleanExpression creditEq(Double credit) {
    return credit == null ? null : officialLecture.credit.eq(credit);
  }

  private BooleanExpression creditGoe(Double credit) {
    return credit == null ? null : officialLecture.credit.goe(credit);
  }
}

