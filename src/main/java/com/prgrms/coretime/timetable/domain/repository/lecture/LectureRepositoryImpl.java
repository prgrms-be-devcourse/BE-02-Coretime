package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.school.domain.QSchool.school;
import static com.prgrms.coretime.timetable.domain.enrollment.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.lecture.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;
import static com.prgrms.coretime.timetable.domain.lectureDetail.QLectureDetail.lectureDetail;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.Day;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            searchConditionBuilder(officialLectureSearchCondition)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(
            officialLectureSort(pageable)
        )
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(officialLecture.count())
        .from(officialLecture)
        .where(
            searchConditionBuilder(officialLectureSearchCondition)
        );

    return PageableExecutionUtils.getPage(officialLectures, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<OfficialLecture> findOfficialLectureById(Long id) {
    return Optional.ofNullable(queryFactory
        .select(officialLecture)
        .distinct()
        .from(officialLecture)
        .join(officialLecture.school, school)
        .fetchJoin()
        .join(officialLecture.lectureDetails, lectureDetail)
        .fetchJoin()
        .where(officialLectureIddEq(id))
        .fetchOne());
  }

  @Override
  public long getNumberOfConflictLectures(Long timetableId, List<LectureDetail> lectureDetails) {
     return queryFactory
        .select(lecture.count())
        .from(lecture)
        .join(lecture.enrollments, enrollment)
        .join(lecture.lectureDetails, lectureDetail)
        .where(
            conflictConditionBuilder(timetableId, lectureDetails)
        )
        .fetchOne();
  }

  private BooleanBuilder searchConditionBuilder(OfficialLectureSearchCondition officialLectureSearchCondition) {
    BooleanBuilder searchConditionBuilder = new BooleanBuilder();
    searchConditionBuilder.and(openYearEq(officialLectureSearchCondition.getOpenYear()));
    searchConditionBuilder.and(semesterEq(officialLectureSearchCondition.getSemester()));

    if(officialLectureSearchCondition.getLectureTypes() != null) {
      BooleanBuilder lectureTypeBuilder = new BooleanBuilder();
      for(LectureType lectureType : officialLectureSearchCondition.getLectureTypes()) {
        lectureTypeBuilder.or(lectureTypeEq(lectureType));
      }
      searchConditionBuilder.and(lectureTypeBuilder);
    }

    if(officialLectureSearchCondition.getGrades() != null) {
      BooleanBuilder gradeBuilder = new BooleanBuilder();
      gradeBuilder.or(gradeEq(Grade.ETC));
      for(Grade grade : officialLectureSearchCondition.getGrades()) {
        gradeBuilder.or(gradeEq(grade));
      }
      searchConditionBuilder.and(gradeBuilder);
    }

    if(officialLectureSearchCondition.getCredits() != null) {
      BooleanBuilder creditBuilder = new BooleanBuilder();
      for(Double credit : officialLectureSearchCondition.getCredits()) {
        if(credit == 4.0) {
          creditBuilder.or(creditGoe(credit));
        }else{
          creditBuilder.or(creditEq(credit));
        }
      }
      searchConditionBuilder.and(creditBuilder);
    }

    if(officialLectureSearchCondition.getSearchType() != null &&
        officialLectureSearchCondition.getSearchWord() != null) {
      SearchType searchType = officialLectureSearchCondition.getSearchType();
      String searchWord = officialLectureSearchCondition.getSearchWord();

      switch (searchType) {
        case name:
          searchConditionBuilder.and(nameContains(searchWord));
          break;
        case professor:
          searchConditionBuilder.and(professorContains(searchWord));
          break;
        case code:
          searchConditionBuilder.and(codeContains(searchWord));
          break;
        default:
          searchConditionBuilder.and(classroomContains(searchWord));
          break;
      }
    }

    return searchConditionBuilder;
  }

  private OrderSpecifier<?> officialLectureSort(Pageable pageable) {
    if(!pageable.getSort().isEmpty()) {
      for(Sort.Order order : pageable.getSort()) {
        switch (order.getProperty()) {
          case "name":
            return new OrderSpecifier(Order.ASC, officialLecture.name);
          case "code":
            return new OrderSpecifier(Order.ASC, officialLecture.code);
          default:
            return new OrderSpecifier(Order.ASC, officialLecture.id);
        }
      }
    }

    return new OrderSpecifier(Order.ASC, officialLecture.id);
  }

  private BooleanBuilder conflictConditionBuilder(Long timetableId, List<LectureDetail> lectureDetails) {
    BooleanBuilder conflictConditionBuilder = new BooleanBuilder();
    BooleanBuilder dayAndTimesBuilder = new BooleanBuilder();

    for(LectureDetail lectureDetail : lectureDetails) {
      BooleanBuilder dayAndTimeBuilder = new BooleanBuilder();

      dayAndTimeBuilder
          .and(dayEq(lectureDetail.getDay()))
          .and(startTimeLt(lectureDetail.getEndTime()))
          .and(endTimeGt(lectureDetail.getStartTime()));
      dayAndTimesBuilder.or(dayAndTimeBuilder);
    }

    conflictConditionBuilder
        .and(timetableIdEq(timetableId))
        .and(dayAndTimesBuilder);

    return conflictConditionBuilder;
  }

  private BooleanExpression officialLectureIddEq(Long officialLectureId) {
    return officialLectureId == null ? null : officialLecture.id.eq(officialLectureId);
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

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }

  private BooleanExpression dayEq(Day day) {
    return day == null ? null : lectureDetail.day.eq(day);
  }

  private BooleanExpression creditGoe(Double credit) {
    return credit == null ? null : officialLecture.credit.goe(credit);
  }

  private BooleanExpression startTimeLt(LocalTime endTime) {
    return endTime == null ? null : lectureDetail.startTime.lt(endTime);
  }

  private BooleanExpression endTimeGt(LocalTime startTime) {
    return startTime == null ? null : lectureDetail.endTime.gt(startTime);
  }

  private BooleanExpression nameContains(String name) {
    return name == null ? null : officialLecture.name.contains(name);
  }

  private BooleanExpression professorContains(String professor) {
    return professor == null ? null : officialLecture.professor.contains(professor);
  }

  private BooleanExpression codeContains(String code) {
    return code == null ? null : officialLecture.code.contains(code);
  }

  private BooleanExpression classroomContains(String classroom) {
    return classroom == null ? null : officialLecture.classroom.contains(classroom);
  }
}

