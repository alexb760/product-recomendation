package com.book.microservices.core.review.services;

import com.book.api.core.review.Review;
import com.book.api.core.review.ReviewService;
import com.book.api.event.Event;
import com.book.util.exception.EventProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/** @author Alexander Bravo */
@Slf4j
@EnableBinding(Sink.class)
public class MessageProcessor {
  private final ReviewService reviewService;

  @Autowired
  public MessageProcessor(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Review> event) {

    log.info("Process message created at {}...", event.getEventCreatedAt());

      switch (event.getEventType()) {
          case CREATE -> {
              Review review = event.getData();
              log.info("Create review with ID: {}/{}", review.getProductId(), review.getReviewId());
              reviewService.createReview(review);
          }
          case DELETE -> {
              int productId = event.getKey();
              log.info("Delete reviews with ProductID: {}", productId);
              reviewService.deleteReviews(productId);
          }
          default -> {
              String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
              log.warn(errorMessage);
              throw new EventProcessingException(errorMessage);
          }
      }

    log.info("Message processing done!");
  }
}
