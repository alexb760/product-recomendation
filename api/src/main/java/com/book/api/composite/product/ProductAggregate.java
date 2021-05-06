package com.book.api.composite.product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alexander Bravo
 */
@AllArgsConstructor
@Getter
public class ProductAggregate {
 private final int productId;
 private final String name;
 private final int weight;
 private final List<RecommendationSummary> recommendations;
 private final List<ReviewSummary> reviews;
 private final ServiceAddresses serviceAddresses;
}
