package com.book.microservices.core.review.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;

/** @author Alexander Bravo */
@Getter
@Setter
@Entity
@Table(
    name = "reviews",
    indexes = {
      @Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId")
    })
public class ReviewEntity {

  @Id @GeneratedValue private int id;

  @Version private int version;

  private int productId;
  private int reviewId;
  private String author;
  private String subject;
  private String content;

  public ReviewEntity() {}

  public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
    this.productId = productId;
    this.reviewId = reviewId;
    this.author = author;
    this.subject = subject;
    this.content = content;
  }
}
