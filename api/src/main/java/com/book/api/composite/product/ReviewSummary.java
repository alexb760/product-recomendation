package com.book.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alexander Bravo
 */
@Getter
@AllArgsConstructor
public class ReviewSummary {
 private final int reviewId;
 private final String author;
 private final String subject;
}
