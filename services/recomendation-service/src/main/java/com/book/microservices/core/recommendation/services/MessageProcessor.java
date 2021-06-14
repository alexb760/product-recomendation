package com.book.microservices.core.recommendation.services;

import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.recomendation.RecommendationService;
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
  private final RecommendationService recommendationService;

  @Autowired
  public MessageProcessor(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Recommendation> event) {

    log.info("Process message created at {}...", event.getEventCreatedAt());

   switch (event.getEventType()) {
    case CREATE -> {
     Recommendation recommendation = event.getData();
     log.info("Create recommendation with ID: {}/{}", recommendation.getProductId(), recommendation.getRecommendationId());
     recommendationService.createRecommendation(recommendation);
    }
    case DELETE -> {
     int productId = event.getKey();
     log.info("Delete recommendations with ProductID: {}", productId);
     recommendationService.deleteRecommendations(productId);
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
