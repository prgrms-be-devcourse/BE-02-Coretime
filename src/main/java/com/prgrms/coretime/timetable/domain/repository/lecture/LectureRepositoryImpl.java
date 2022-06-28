package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.school.domain.QSchool.school;
import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;
import static com.prgrms.coretime.timetable.domain.lectureDetail.QLectureDetail.lectureDetail;

import com.prgrms.coretime.school.domain.QSchool;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    // TODO : 정렬, ID(기본), name or code => 모두 asc

    List<OfficialLecture> officialLectures = queryFactory
        .selectFrom(officialLecture)
        .where(
            searchCondition(officialLectureSearchCondition)
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
            searchCondition(officialLectureSearchCondition)
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
        .join(officialLecture.lectureDetails)
        .fetchJoin()
        .where(idEq(id))
        .fetchOne());
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
        if(credit == 4.0) {
          creditBuilder.or(creditGoe(credit));
        }else{
          creditBuilder.or(creditEq(credit));
        }
      }
      builder.and(creditBuilder);
    }

    if(officialLectureSearchCondition.getSearchType() != null &&
        officialLectureSearchCondition.getSearchWord() != null) {
      SearchType searchType = officialLectureSearchCondition.getSearchType();
      String searchWord = officialLectureSearchCondition.getSearchWord();

      switch (searchType) {
        case name:
          builder.and(nameContains(searchWord));
          break;
        case professor:
          builder.and(professorContains(searchWord));
          break;
        case code:
          builder.and(codeContains(searchWord));
          break;
        default:
          builder.and(classroomContains(searchWord));
          break;
      }
    }

    return builder;
  }

  private BooleanExpression idEq(Long id) {
    return id == null ? null : officialLecture.id.eq(id);
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

