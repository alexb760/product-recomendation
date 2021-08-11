/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.microservices.composite.product;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Class to allow any interaction with API and disable OAuth2.
 *
 * @author Alexander Bravo
 */

@TestConfiguration(value = "springSecurityFilterChain1")
public class TestSecurityConfig {
 @Bean
 public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
  http.csrf().disable().authorizeExchange().anyExchange().permitAll();
  return http.build();
 }
}
