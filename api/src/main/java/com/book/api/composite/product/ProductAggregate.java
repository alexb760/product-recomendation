package com.book.api.composite.product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alexander Bravo
 */
@AllArgsConstructor
@Getter
@Setter
public class ProductAggregate {
 private int productId;
 private String name;
 private int weight;
 private List<RecommendationSummary> recommendations;
 private List<ReviewSummary> reviews;
 private ServiceAddresses serviceAddresses;

  public ProductAggregate() {
    this.productId = 0;
    this.name = " ";
    this.weight = 0;
    this.recommendations = null;
    this.reviews = null;
    this.serviceAddresses = null;
  }
}
