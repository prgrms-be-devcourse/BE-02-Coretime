package com.prgrms.coretime.timetable.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = TimeFormatValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface TimeFormatConstraint {
  String message() default "잘못된 시간 포맷 입니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
