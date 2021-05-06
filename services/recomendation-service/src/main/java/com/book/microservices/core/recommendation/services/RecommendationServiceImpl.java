package com.book.microservices.core.recommendation.services;

import com.book.api.core.recomendation.Recomendation;
import com.book.api.core.recomendation.RecommendationService;
import com.book.util.exception.InvalidInputException;
import com.book.util.http.ServiceUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Bravo
 */
@Slf4j
@RestController
public class RecommendationServiceImpl implements RecommendationService {

 private final ServiceUtil serviceUtil;

 @Autowired
 public RecommendationServiceImpl(ServiceUtil serviceUtil) {
  this.serviceUtil = serviceUtil;
 }

  @Override
  public List<Recomendation> getRecommendations(int productId) {

    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

    if (productId == 113) {
      log.debug("No recommendations found for productId: {}", productId);
      return List.of();
    }

    List<Recomendation> list =
        List.of(
            new Recomendation(
                productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()),
            new Recomendation(
                productId, 2, "Author 1", 2, "Content 2", serviceUtil.getServiceAddress()),
            new Recomendation(
                productId, 3, "Author 1", 3, "Content 3", serviceUtil.getServiceAddress()));

    log.debug("/recommendation response size: {}", list.size());

    return list;
  }
 }
