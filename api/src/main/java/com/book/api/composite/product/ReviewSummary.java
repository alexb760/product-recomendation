package com.book.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alexander Bravo
 */
@Getter
@Setter
@AllArgsConstructor
public class ReviewSummary {
 private int reviewId;
 private String author;
 private String content;
 private String subject;

 public ReviewSummary() {
  this.reviewId = 0;
  this.author = " ";
  this.content = " ";
  this.subject = " ";
 }
}
