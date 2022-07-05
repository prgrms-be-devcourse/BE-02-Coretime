package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.school.domain.QSchool.school;
import static com.prgrms.coretime.timetable.domain.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.QLectureDetail.lectureDetail;
import static com.prgrms.coretime.timetable.domain.QOfficialLecture.officialLecture;
import static java.util.Objects.nonNull;

import com.prgrms.coretime.timetable.domain.Day;
import com.prgrms.coretime.timetable.domain.Grade;
import com.prgrms.coretime.timetable.domain.Lecture;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import com.prgrms.coretime.timetable.domain.LectureType;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import com.prgrms.coretime.timetable.util.FlushAndClear;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<OfficialLecture> getOfficialLectures(OfficialLectureSearchCondition officialLectureSearchCondition, Pageable pageable) {
    BooleanBuilder searchCondition = getSearchCondition(officialLectureSearchCondition);

    List<OfficialLecture> officialLectures = queryFactory
        .selectFrom(officialLecture)
        .where(searchCondition)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(officialLectureSort(pageable))
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(officialLecture.count())
        .from(officialLecture)
        .where(searchCondition);

    return PageableExecutionUtils.getPage(officialLectures, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<OfficialLecture> getOfficialLectureById(Long id) {
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
  public long getNumberOfTimeOverlapLectures(Long timetableId, List<LectureDetail> lectureDetails, List<Long> lectureDetailIds) {
    BooleanBuilder overlapCondition = getOverlapCondition(timetableId, lectureDetails, lectureDetailIds);

     return queryFactory
        .select(lecture.count())
        .from(lecture)
        .join(lecture.enrollments, enrollment)
        .join(lecture.lectureDetails, lectureDetail)
        .where(overlapCondition)
        .fetchOne();
  }

  @Override
  public boolean isCustomLecture(Long lectureId) {
    BooleanBuilder customLectureCondition = getCustomLectureConditionBuilder(lectureId);

    Lecture customLecture = queryFactory
        .select(lecture)
        .from(lecture)
        .where(customLectureCondition)
        .fetchOne();

    return nonNull(customLecture);
  }

  @Override
  @FlushAndClear
  public void deleteLectureByLectureIds(List<Long> lectureIds) {
    queryFactory
        .delete(lecture)
        .where(lecture.id.in(lectureIds))
        .execute();
  }

  private BooleanBuilder getSearchCondition(OfficialLectureSearchCondition officialLectureSearchCondition) {
    BooleanBuilder searchCondition = new BooleanBuilder();

    searchCondition
        .and(officialLectureSchoolIdEq(officialLectureSearchCondition.getSchoolId()))
        .and(officialLectureYearEq(officialLectureSearchCondition.getOpenYear()))
        .and(officialLectureSemesterEq(officialLectureSearchCondition.getSemester()));

    if(nonNull(officialLectureSearchCondition.getLectureTypes())) {
      BooleanBuilder lectureTypeCondition = new BooleanBuilder();
      officialLectureSearchCondition.getLectureTypes().forEach(lectureType -> lectureTypeCondition.or(officialLectureLectureTypeEq(lectureType)));
      searchCondition.and(lectureTypeCondition);
    }

    if(nonNull(officialLectureSearchCondition.getGrades())) {
      BooleanBuilder gradeCondition = new BooleanBuilder();
      gradeCondition.or(officialLectureGradeEq(Grade.ETC));
      officialLectureSearchCondition.getGrades().forEach(grade -> gradeCondition.or(officialLectureGradeEq(grade)));
      searchCondition.and(gradeCondition);
    }

    if(nonNull(officialLectureSearchCondition.getCredits())) {
      BooleanBuilder creditCondition = new BooleanBuilder();
      officialLectureSearchCondition.getCredits()
          .forEach(credit -> creditCondition.or(credit == 4.0 ? officialLectureCreditGoe(credit) : officialLectureCreditEq(credit)));
      searchCondition.and(creditCondition);
    }

    if(nonNull(officialLectureSearchCondition.getSearchType()) && nonNull(officialLectureSearchCondition.getSearchWord())) {
      SearchType searchType = officialLectureSearchCondition.getSearchType();
      String searchWord = officialLectureSearchCondition.getSearchWord();

      switch (searchType) {
        case name:
          searchCondition.and(officialLectureNameContains(searchWord));
          break;
        case professor:
          searchCondition.and(officialLectureProfessorContains(searchWord));
          break;
        case code:
          searchCondition.and(officialLectureCodeContains(searchWord));
          break;
        default:
          searchCondition.and(officialLectureClassroomContains(searchWord));
          break;
      }
    }

    return searchCondition;
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

  private BooleanBuilder getOverlapCondition(Long timetableId, List<LectureDetail> lectureDetails, List<Long> lectureDetailIds) {
    BooleanBuilder overLapCondition = new BooleanBuilder();
    BooleanBuilder dayAndTimesCondition = new BooleanBuilder();
    BooleanBuilder lectureDetailIdsCondition = new BooleanBuilder();

    for(LectureDetail lectureDetail : lectureDetails) {
      BooleanBuilder dayAndTimeCondition = new BooleanBuilder();

      dayAndTimeCondition
          .and(lectureDetailDayEq(lectureDetail.getDay()))
          .and(startTimeLt(lectureDetail.getEndTime()))
          .and(endTimeGt(lectureDetail.getStartTime()));
      dayAndTimesCondition.or(dayAndTimeCondition);
    }

    for(Long lectureDetailId : lectureDetailIds) {
      lectureDetailIdsCondition.and(lectureDetailIdNe(lectureDetailId));
    }

    overLapCondition
        .and(enrollmentTimetableIdEq(timetableId))
        .and(dayAndTimesCondition)
        .and(lectureDetailIdsCondition);

    return overLapCondition;
  }

  private BooleanBuilder getCustomLectureConditionBuilder(Long lectureId) {
    BooleanBuilder customLectureExistConditionBuilder = new BooleanBuilder();
    customLectureExistConditionBuilder
        .and(lectureIdEq(lectureId))
        .and(lecture.dType.eq("CUSTOM"));

    return customLectureExistConditionBuilder;
  }

  private BooleanExpression officialLectureIddEq(Long officialLectureId) {
    return officialLectureId == null ? null : officialLecture.id.eq(officialLectureId);
  }

  private BooleanExpression officialLectureSchoolIdEq(Long schoolId) {
    return schoolId == null ? null : officialLecture.school.id.eq(schoolId);
  }

  private BooleanExpression officialLectureYearEq(Integer year) {
    return year == null ? null : officialLecture.openYear.eq(year);
  }

  private BooleanExpression officialLectureSemesterEq(Semester semester) {
    return semester == null ? null : officialLecture.semester.eq(semester);
  }

  private BooleanExpression officialLectureLectureTypeEq(LectureType lectureType) {
    return lectureType == null ? null : officialLecture.lectureType.eq(lectureType);
  }

  private BooleanExpression officialLectureGradeEq(Grade grade) {
    return grade == null ? null : officialLecture.grade.eq(grade);
  }

  private BooleanExpression officialLectureCreditEq(Double credit) {
    return credit == null ? null : officialLecture.credit.eq(credit);
  }

  private BooleanExpression officialLectureCreditGoe(Double credit) {
    return credit == null ? null : officialLecture.credit.goe(credit);
  }

  private BooleanExpression officialLectureNameContains(String name) {
    return  name == null ? null : officialLecture.name.contains(name);
  }

  private BooleanExpression officialLectureProfessorContains(String professor) {
    return professor == null ? null : officialLecture.professor.contains(professor);
  }

  private BooleanExpression officialLectureCodeContains(String code) {
    return code == null ? null : officialLecture.code.contains(code);
  }

  private BooleanExpression officialLectureClassroomContains(String classroom) {
    return classroom == null ? null : officialLecture.classroom.contains(classroom);
  }

  private BooleanExpression enrollmentTimetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }

  private BooleanExpression lectureDetailDayEq(Day day) {
    return day == null ? null : lectureDetail.day.eq(day);
  }

  private BooleanExpression lectureDetailIdNe(Long lectureDetailId) {
    return lectureDetailId == null ? null : lectureDetail.id.ne(lectureDetailId);
  }

  private BooleanExpression lectureIdEq(Long lectureId) {
    return lectureId == null ? null : lecture.id.eq(lectureId);
  }

  private BooleanExpression startTimeLt(LocalTime endTime) {
    return endTime == null ? null : lectureDetail.startTime.lt(endTime);
  }

  private BooleanExpression endTimeGt(LocalTime startTime) {
    return startTime == null ? null : lectureDetail.endTime.gt(startTime);
  }
}

