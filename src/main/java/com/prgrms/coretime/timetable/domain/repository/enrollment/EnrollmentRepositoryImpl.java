package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentCustomRepository{
  private final JPAQueryFactory queryFactory;

}
