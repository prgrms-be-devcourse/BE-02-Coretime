package com.prgrms.coretime.timetable.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class TimeFormatValidator implements ConstraintValidator<TimeFormatConstraint, String> {

  @Override
  public void initialize(TimeFormatConstraint constraintAnnotation) {
  }

  @Override
  public boolean isValid(String time, ConstraintValidatorContext context) {
    if(!StringUtils.hasText(time)) {
      return false;
    }

    Pattern pattern = Pattern.compile("^(([0-1]{1}[0-9]{1})|([2]{1}[0-3]{1})):(([0-5]{1}[0|5]{1}))$");
    Matcher matcher = pattern.matcher(time);
    return matcher.matches();
  }
}
