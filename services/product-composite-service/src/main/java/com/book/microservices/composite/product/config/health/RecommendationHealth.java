/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.microservices.composite.product.config.health;

import com.book.microservices.composite.product.services.ProductCompositeIntegration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Alexander Bravo
 */

@Slf4j
@Component
public class RecommendationHealth implements ReactiveHealthIndicator, ReactiveHealthContributor {

 private  final ProductCompositeIntegration integration;
 private final WebClient webClient;

 @Autowired
 public RecommendationHealth(ProductCompositeIntegration integration, WebClient.Builder webClient) {
  this.integration = integration;
  this.webClient = webClient.build();
 }

 @Override
 public Mono<Health> health() {
    return getHealth(integration.getRecommendationServiceUrl());
 }

 private Mono<Health> getHealth(String url) {
  url += "/actuator/health"; //Bad practice
  log.debug("Will call the Health API on URL: {}", url);
  return webClient.get()
      .uri(url)
      .retrieve()
      .bodyToMono(String.class)
      .map(s -> new Health.Builder().up().build())
      .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
      .log();
 }
}
