package com.prgrms.coretime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CoretimeApplication {

  public static void main(String[] args) {
    SpringApplication.run(CoretimeApplication.class, args);
  }

}
