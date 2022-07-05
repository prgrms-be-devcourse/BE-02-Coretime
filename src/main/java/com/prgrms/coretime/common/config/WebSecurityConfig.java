package com.prgrms.coretime.common.config;

import com.prgrms.coretime.common.jwt.Jwt;
import com.prgrms.coretime.common.jwt.JwtAuthenticationFilter;
import com.prgrms.coretime.common.jwt.JwtAuthenticationProvider;
import com.prgrms.coretime.common.util.JwtService;
import com.prgrms.coretime.common.util.RedisService;
import com.prgrms.coretime.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtConfig jwtConfig;

  public WebSecurityConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Bean
  public Jwt jwt() {
    return new Jwt(
        jwtConfig.getIssuer(),
        jwtConfig.getClientSecret(),
        jwtConfig.getExpirySeconds());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService, UserService userService) {
    return new JwtAuthenticationProvider(jwtService, userService);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    Jwt jwt = getApplicationContext().getBean(Jwt.class);
    return new JwtAuthenticationFilter(jwtConfig.getHeader(), jwt);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/assets/**", "/h2-console/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/swagger*/**").permitAll()
        .antMatchers("/api/v1/users/local/register", "/api/v1/users/local/login").permitAll()
        .antMatchers("/api/v1/users/oauth/register", "/api/v1/users/oauth/login").permitAll()
        .antMatchers("/api/v1/**").hasAuthority("USER")
        .and()
        .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class);
  }

  /*TODO: AccessDeniedHandler*/
}
