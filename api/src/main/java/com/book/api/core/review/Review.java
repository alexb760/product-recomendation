package com.book.api.core.review;

import lombok.Getter;
import lombok.Setter;

/** @author Alexander Bravo */
@Getter
@Setter
public class Review {
  private int productId;
  private int reviewId;
  private String author;
  private String subject;
  private String content;
  private String serviceAddress;

  public Review() {
    productId = 0;
    reviewId = 0;
    author = null;
    subject = null;
    content = null;
    serviceAddress = null;
  }

  public Review(
      int productId,
      int reviewId,
      String author,
      String subject,
      String content,
      String serviceAddress) {
    this.productId = productId;
    this.reviewId = reviewId;
    this.author = author;
    this.subject = subject;
    this.content = content;
    this.serviceAddress = serviceAddress;
  }
}
