package com.book.microservices.core.recommendation.services;

import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.recomendation.RecommendationService;
import com.book.microservices.core.recommendation.persistence.RecommendationEntity;
import com.book.microservices.core.recommendation.persistence.RecommendationRepository;
import com.book.util.exception.InvalidInputException;
import com.book.util.http.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    // for the Blocking API call refer to git tag v1.0.0-cap6
    if (body.getProductId() < 1)
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    RecommendationEntity entity = mapper.apiToEntity(body);
    Mono<Recommendation> newEntity = repository
            .save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException(
                        String.format("Duplicate key, Product Id: %s, Recommendation Id: %s",
                            body.getProductId(), body.getRecommendationId())))
            .map(mapper::entityToApi);

    return newEntity.block();
  }

  @Override
  public Flux<Recommendation> getRecommendations(int productId) {

    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
    // --- Blocking Call ----//
    //  Blocking API call refer to git tag v1.0.0-cap6

    // --- Non Blocking call ---//
    return repository.findByProductId(productId)
        .log()
        .map(mapper::entityToApi)
        .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
  }

  @Override
  public void deleteRecommendations(int productId) {
    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

    log.debug(
        "deleteRecommendations: tries to delete recommendations for the product with productId: {}",
        productId);
    repository.deleteAll(repository.findByProductId(productId)).block();
  }
}
