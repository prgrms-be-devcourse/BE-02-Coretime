package com.prgrms.coretime.timetable.util;

import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class AfterBulkAspect {
  private final EntityManager entityManager;

  @After("@annotation(FlushAndClear)")
  public void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }
}
