package com.book.microservices.core.recommendation.services;

import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.recomendation.RecommendationService;
import com.book.microservices.core.recommendation.persistence.RecommendationEntity;
import com.book.microservices.core.recommendation.persistence.RecommendationRepository;
import com.book.util.exception.InvalidInputException;
import com.book.util.http.ServiceUtil;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

/** @author Alexander Bravo */
@Slf4j
@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private final ServiceUtil serviceUtil;
  private final RecommendationRepository repository;
  private final RecommendationMapper mapper;

  @Autowired
  public RecommendationServiceImpl(
      ServiceUtil serviceUtil, RecommendationRepository repository, RecommendationMapper mapper) {
    this.serviceUtil = serviceUtil;
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      RecommendationEntity entity = mapper.apiToEntity(body);
      RecommendationEntity saved = repository.save(entity);
      log.debug(
          "createRecommendation: created a recommendation entity: {}/{}",
          body.getProductId(),
          body.getRecommendationId());

      return mapper.entityToApi(saved);
    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException(
          String.format(
              "Duplicate Key, producId: %s recommendationId: %s",
              body.getProductId(), body.getRecommendationId()));
    }
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {

    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
    List<RecommendationEntity> entities = repository.findByProductId(productId);
    List<Recommendation> recommendations = mapper.entityListToApiList(entities);
    final String serviceAddress = serviceUtil.getServiceAddress();
    log.debug("/recommendation response size: {}", recommendations.size());

    return recommendations.stream()
        .map(getRecommendationFunction(serviceAddress))
        .collect(Collectors.toList());
  }

  private Function<Recommendation, Recommendation> getRecommendationFunction(String serviceAddress) {
    return rec ->
        new Recommendation(
            rec.getProductId(),
            rec.getRecommendationId(),
            rec.getAuthor(),
            rec.getRate(),
            rec.getContent(),
            serviceAddress);
  }

  @Override
  public void deleteRecommendations(int productId) {
    repository.deleteAll(repository.findByProductId(productId));
  }
}
