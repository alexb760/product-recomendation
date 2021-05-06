package com.book.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** @author Alexander Bravo */
@AllArgsConstructor
@Getter
public class RecommendationSummary {
  private final int recommendationId;
  private final String author;
  private final int rate;
}
