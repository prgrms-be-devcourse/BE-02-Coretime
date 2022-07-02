package com.prgrms.coretime.timetable.domain;

import lombok.Getter;

@Getter
public enum Semester {
  FIRST(0), SUMMER(1), SECOND(2), WINTER(3);

  private int order;

  Semester(int order) {
    this.order = order;
  }
}
