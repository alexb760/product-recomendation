package com.book.microservices.core.review.services;

import com.book.api.core.review.Review;
import com.book.api.core.review.ReviewService;
import com.book.util.exception.InvalidInputException;
import com.book.util.http.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/** @author Alexander Bravo */
@Slf4j
@RestController
public class ReviewServiceImpl implements ReviewService {

  private final ServiceUtil serviceUtil;

  @Autowired
  public ReviewServiceImpl(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public List<Review> getReviews(int productId) {

    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

    if (productId == 213) {
      log.debug("No reviews found for productId: {}", productId);
      return List.of();
    }

    List<Review> list = new ArrayList<>();
    list.add(
        new Review(
            productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
    list.add(
        new Review(
            productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
    list.add(
        new Review(
            productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

    log.debug("/reviews response size: {}", list.size());

    return list;
  }
}
