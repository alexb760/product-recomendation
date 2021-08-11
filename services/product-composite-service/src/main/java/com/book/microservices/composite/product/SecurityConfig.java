/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.microservices.composite.product;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Java class to control access resources.
 *
 * @author Alexander Bravo
 */
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange()
        .pathMatchers("/actuator/**")
        .permitAll()
        .pathMatchers(POST, "/product-composite/**")
        .hasAuthority("SCOPE_product:write")
        .pathMatchers(DELETE, "/product-composite/**")
        .hasAuthority("SCOPE_product:write")
        .pathMatchers(GET, "/product-composite/**")
        .hasAuthority("SCOPE_product:read")
        .anyExchange()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();
    return http.build();
  }
}
