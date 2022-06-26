package com.prgrms.coretime.timetable.domain.lecture;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_lecture")
@DiscriminatorValue("CUSTOM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CustomLecture extends Lecture {

  @Builder
  public CustomLecture(String name, String professor, String classroom) {
    super(name, professor, classroom);
  }
}
