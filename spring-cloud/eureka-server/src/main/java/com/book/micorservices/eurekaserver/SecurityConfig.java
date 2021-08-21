package com.book.micorservices.eurekaserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/**
 * @author Alexander Bravo
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

 private final String userName;
 private final String password;

 public SecurityConfig(
     @Value("${app.eureka-username}") String userName,
     @Value("${app.eureka-password}")String password) {
  this.userName = userName;
  this.password = password;
 }

 @Override
 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth.inMemoryAuthentication()
      .passwordEncoder(NoOpPasswordEncoder.getInstance())
      .withUser(userName).password(password)
      .authorities("USER");
 }

 @Override
 protected void configure(HttpSecurity http) throws Exception {
  http
      // Disable CRCF to allow services to register themselves with Eureka
      .csrf().disable()
      .authorizeRequests()
      .anyRequest().authenticated()
      .and()
      .httpBasic();
 }
}
