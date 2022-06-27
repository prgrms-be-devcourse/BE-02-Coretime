package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.timetable.domain.lecture.QOfficialLecture.officialLecture;

import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
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
  public Page<OfficialLecture> findOfficialLectures(
      OfficialLectureSearchCondition condition, Pageable pageable) {

    log.info("{}", pageable.getOffset());
    log.info("{}", pageable.getPageSize());

    List<OfficialLecture> officialLectures = queryFactory
        .selectFrom(officialLecture)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(officialLecture.count())
        .from(officialLecture);

    return PageableExecutionUtils.getPage(officialLectures, pageable, countQuery::fetchOne);
  }
}
