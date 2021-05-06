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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Alexander Bravo */
public interface RecommendationService {

  /**
   * Sample usage: curl $HOST:$PORT/recommendation?productId=1
   *
   * @param productId product identifier
   * @return List of {@link Recomendation}
   */
  @GetMapping(value = "/recomendation", produces = MediaType.APPLICATION_JSON_VALUE)
  List<Recomendation> getRecommendations(
      @RequestParam(value = "productId", required = true) int productId);
}
