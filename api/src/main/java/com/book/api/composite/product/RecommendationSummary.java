package com.book.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** @author Alexander Bravo */
@AllArgsConstructor
@Getter
@Setter
public class RecommendationSummary {
  private int recommendationId;
  private String author;
  private String content;
  private int rate;

  public RecommendationSummary() {
    this.recommendationId = 0;
    this.author = " ";
    this.content = " ";
    this.rate = 0;
  }
}
