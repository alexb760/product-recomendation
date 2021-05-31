/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.api.core.recomendation;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

/** @author Alexander Bravo */
public interface RecommendationService {


  /**
   * Sample usage:
   *
   * curl -X POST $HOST:$PORT/recommendation \
   *   -H "Content-Type: application/json" --data \
   *   '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
   *
   * @param body
   * @return
   */
  @PostMapping(
      value    = "/recommendation",
      consumes = "application/json",
      produces = "application/json")
  Recommendation createRecommendation(@RequestBody Recommendation body);


  /**
   * Sample usage: curl $HOST:$PORT/recommendation?productId=1
   *
   * @param productId product identifier
   * @return List of {@link Recommendation}
   */
  @GetMapping(value = "/recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
  Flux<Recommendation> getRecommendations(
      @RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage:
   *
   * curl -X DELETE $HOST:$PORT/recommendation?productId=1
   *
   * @param productId product identifier
   */
  @DeleteMapping(value = "/recommendation")
  void deleteRecommendations(@RequestParam(value = "productId", required = true)  int productId);
}
