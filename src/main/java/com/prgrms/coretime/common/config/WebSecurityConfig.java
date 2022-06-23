package com.prgrms.coretime.common.config;

import com.prgrms.coretime.common.jwt.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfigure jwtConfigure;

    public WebSecurityConfig(JwtConfigure jwtConfigure) {
        this.jwtConfigure = jwtConfigure;
    }

    @Bean
    public Jwt jwt() {
        return new Jwt(
            jwtConfigure.getIssuer(),
            jwtConfigure.getClientSecret(),
            jwtConfigure.getExpirySeconds()
        );
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
                .antMatchers("/api/v1/**").permitAll();
    }
}
