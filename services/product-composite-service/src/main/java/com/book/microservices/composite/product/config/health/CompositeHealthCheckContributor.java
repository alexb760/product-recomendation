/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.microservices.composite.product.config.health;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.stereotype.Component;

/** @author Alexander Bravo */
@Slf4j
//@Component("CheckHealthComponent")
public class CompositeHealthCheckContributor implements CompositeReactiveHealthContributor {

  private final Map<String, ReactiveHealthContributor> contrubutors = new LinkedHashMap<>();

  public CompositeHealthCheckContributor(
      ProductHealth productHealth,
      RecommendationHealth recommendationHealth,
      ReviewHealth reviewHealth) {
    super();
    contrubutors.put("products", productHealth);
    contrubutors.put("recommendation", recommendationHealth);
    contrubutors.put("review", reviewHealth);
  }

  @Override
  public ReactiveHealthContributor getContributor(String name) {
    return contrubutors.get(name);
  }

  @Override
  public Iterator<NamedContributor<ReactiveHealthContributor>> iterator() {
    return contrubutors.entrySet().stream()
        .map(entry -> NamedContributor.of(entry.getKey(), entry.getValue()))
        .iterator();
  }
}
