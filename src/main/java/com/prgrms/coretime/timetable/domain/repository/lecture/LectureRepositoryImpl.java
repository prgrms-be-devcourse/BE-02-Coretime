package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.school.domain.QSchool.school;
import static com.prgrms.coretime.timetable.domain.enrollment.QEnrollment.enrollment;
import static com.prgrms.coretime.timetable.domain.lecture.QLecture.lecture;
import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;
import static com.prgrms.coretime.timetable.domain.lectureDetail.QLectureDetail.lectureDetail;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.prgrms.coretime.timetable.domain.Grade;
import com.prgrms.coretime.timetable.domain.Lecture;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.Day;
import com.prgrms.coretime.timetable.domain.LectureDetail;
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
  public long getNumberOfTimeOverlapLectures(Long timetableId, List<LectureDetail> lectureDetails) {
     return queryFactory
        .select(lecture.count())
        .from(lecture)
        .join(lecture.enrollments, enrollment)
        .join(lecture.lectureDetails, lectureDetail)
        .where(
            getConflictConditionBuilder(timetableId, lectureDetails)
        )
        .fetchOne();
  }

  @Override
  public boolean isCustomLecture(Long lectureId) {
    Lecture customLecture = queryFactory
        .select(lecture)
        .from(lecture)
        .where(
            getCustomLectureConditionBuilder(lectureId)
        )
        .fetchOne();

    return !isNull(customLecture);
  }

  @Override
  @Modifying(clearAutomatically = true)
  public void deleteLectureByLectureIds(List<Long> lectureIds) {
    queryFactory
        .delete(lecture)
        .where(lecture.id.in(lectureIds))
        .execute();
  }

  private BooleanBuilder getSearchCondition(OfficialLectureSearchCondition officialLectureSearchCondition) {
    BooleanBuilder searchCondition = new BooleanBuilder();

    searchCondition
        .and(officialLecture.school.id.eq(officialLectureSearchCondition.getSchoolId()))
        .and(officialLecture.openYear.eq(officialLectureSearchCondition.getOpenYear()))
        .and(officialLecture.semester.eq(officialLectureSearchCondition.getSemester()));

    if(nonNull(officialLectureSearchCondition.getLectureTypes())) {
      BooleanBuilder lectureTypeCondition = new BooleanBuilder();
      officialLectureSearchCondition.getLectureTypes().forEach(lectureType -> lectureTypeCondition.or(officialLecture.lectureType.eq(lectureType)));
      searchCondition.and(lectureTypeCondition);
    }

    if(nonNull(officialLectureSearchCondition.getGrades())) {
      BooleanBuilder gradeCondition = new BooleanBuilder();
      gradeCondition.or(officialLecture.grade.eq(Grade.ETC));
      officialLectureSearchCondition.getGrades().forEach(grade -> gradeCondition.or(officialLecture.grade.eq(grade)));
      searchCondition.and(gradeCondition);
    }

    if(nonNull(officialLectureSearchCondition.getCredits())) {
      BooleanBuilder creditCondition = new BooleanBuilder();
      officialLectureSearchCondition.getCredits()
          .forEach(credit -> creditCondition.or(credit == 4.0 ? officialLecture.credit.goe(credit) : officialLecture.credit.eq(credit)));
      searchCondition.and(creditCondition);
    }

    if(nonNull(officialLectureSearchCondition.getSearchType()) &&
        nonNull(officialLectureSearchCondition.getSearchWord())) {
      SearchType searchType = officialLectureSearchCondition.getSearchType();
      String searchWord = officialLectureSearchCondition.getSearchWord();

      switch (searchType) {
        case name:
          searchCondition.and(officialLecture.name.contains(searchWord));
          break;
        case professor:
          searchCondition.and(officialLecture.professor.contains(searchWord));
          break;
        case code:
          searchCondition.and(officialLecture.code.contains(searchWord));
          break;
        default:
          searchCondition.and(officialLecture.classroom.contains(searchWord));
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

  private BooleanBuilder getConflictConditionBuilder(Long timetableId, List<LectureDetail> lectureDetails) {
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

  private BooleanBuilder getCustomLectureConditionBuilder(Long lectureId) {
    BooleanBuilder customLectureExistConditionBuilder = new BooleanBuilder();
    customLectureExistConditionBuilder
        .and(lectureIdEq(lectureId))
        .and(customDTypeEq());

    return customLectureExistConditionBuilder;
  }

  private BooleanExpression officialLectureIddEq(Long officialLectureId) {
    return officialLectureId == null ? null : officialLecture.id.eq(officialLectureId);
  }

  private BooleanExpression timetableIdEq(Long timetableId) {
    return timetableId == null ? null : enrollment.enrollmentId.timeTableId.eq(timetableId);
  }

  private BooleanExpression dayEq(Day day) {
    return day == null ? null : lectureDetail.day.eq(day);
  }

  private BooleanExpression lectureIdEq(Long lectureId) {
    return lectureId == null ? null : lecture.id.eq(lectureId);
  }

  private BooleanExpression customDTypeEq() {
    return lecture.dType.eq("CUSTOM");
  }

  private BooleanExpression startTimeLt(LocalTime endTime) {
    return endTime == null ? null : lectureDetail.startTime.lt(endTime);
  }

  private BooleanExpression endTimeGt(LocalTime startTime) {
    return startTime == null ? null : lectureDetail.endTime.gt(startTime);
  }
}

