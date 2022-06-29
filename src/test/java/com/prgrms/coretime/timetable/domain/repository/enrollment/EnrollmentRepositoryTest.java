package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

class EnrollmentRepositoryTest {
  @TestConfiguration
  static class TestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }
}