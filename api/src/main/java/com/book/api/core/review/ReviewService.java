/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.api.core.review;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Alexander Bravo */
public interface ReviewService {

  /**
   * Sample usage:
   *
   * <p>curl -X POST $HOST:$PORT/review \ -H "Content-Type: application/json" --data \
   * '{"productId":123,"reviewId":456,"author":"me","subject":"yada, yada, yada","content":"yada,
   * yada, yada"}'
   *
   * @param body
   * @return
   */
  @PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
  Review createReview(@RequestBody Review body);

  /**
   * Sample usage: curl $HOST:$PORT/review?productId=1
   *
   * @param productId
   * @return
   */
  @GetMapping(value = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
  List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage:
   *
   * <p>curl -X DELETE $HOST:$PORT/review?productId=1
   *
   * @param productId
   */
  @DeleteMapping(value = "/review")
  void deleteReviews(@RequestParam(value = "productId", required = true) int productId);
}
