
package com.prgrms.coretime.school.service;

import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SchoolService {

  private final SchoolRepository schoolRepository;

  public SchoolService(
      SchoolRepository schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  public List<School> findAll() {
    return schoolRepository.findAll();
  }
}
