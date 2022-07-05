package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

import static com.prgrms.coretime.timetable.domain.QLectureDetail.lectureDetail;

import com.prgrms.coretime.timetable.util.FlushAndClear;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LectureDetailRepositoryImpl implements LectureDetailCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  @FlushAndClear
  public void deleteCustomLectureDetailsByLectureId(Long lectureId) {
    queryFactory
        .delete(lectureDetail)
        .where(lectureDetailLectureIdEq(lectureId))
        .execute();
  }

  @Override
  @FlushAndClear
  public void deleteLectureDetailsByLectureIds(List<Long> lectureIds) {
    queryFactory
        .delete(lectureDetail)
        .where(lectureDetailLectureIdIn(lectureIds))
        .execute();
  }

  private BooleanExpression lectureDetailLectureIdEq(Long lectureId) {
    return lectureId == null ? null : lectureDetail.lecture.id.eq(lectureId);
  }

  private BooleanExpression lectureDetailLectureIdIn(List<Long> lectureIds) {
    return lectureIds == null ? null : lectureDetail.lecture.id.in(lectureIds);
  }
}
