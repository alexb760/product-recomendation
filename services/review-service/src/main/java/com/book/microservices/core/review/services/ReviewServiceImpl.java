package com.book.microservices.core.review.services;

import com.book.api.core.review.Review;
import com.book.api.core.review.ReviewService;
import com.book.microservices.core.review.persistence.ReviewEntity;
import com.book.microservices.core.review.persistence.ReviewRepository;
import com.book.util.exception.InvalidInputException;
import com.book.util.http.ServiceUtil;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

/** @author Alexander Bravo */
@Slf4j
@RestController
public class ReviewServiceImpl implements ReviewService {

  private final Scheduler scheduler;
  private final ServiceUtil serviceUtil;
  private final ReviewMapper mapper;
  private final ReviewRepository repository;

  @Autowired
  public ReviewServiceImpl(
      Scheduler scheduler, ServiceUtil serviceUtil, ReviewMapper mapper, ReviewRepository repository) {
    this.scheduler = scheduler;
    this.serviceUtil = serviceUtil;
    this.mapper = mapper;
    this.repository = repository;
  }

  @Override
  public Review createReview(Review body) {
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity saved = repository.save(entity);

      log.debug(
          "createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(saved);
    } catch (DataIntegrityViolationException exp) {
      throw new InvalidInputException(
          String.format(
              "Duplicate key, Product Id: %s, Review Id: %s",
              body.getProductId(), body.getReviewId()));
    }
  }

  @Override
  public Flux<Review> getReviews(int productId) {
    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
    return asyncFlux(() -> Flux.fromIterable(getByProductId(productId)).log(null, Level.FINE));
  }

  protected List<Review> getByProductId(int productId) {

    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<Review> list = mapper.entityListToApiList(entityList);
    final String serviceAddress = serviceUtil.getServiceAddress();
//    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    log.debug("getReviews: response size: {}", list.size());

    return list.stream().map(getReviewAddressFunction(serviceAddress)).collect(Collectors.toList());
  }

  private Function<Review, Review> getReviewAddressFunction(String serviceAddress) {
    return review ->
        new Review(
            review.getProductId(),
            review.getReviewId(),
            review.getAuthor(),
            review.getSubject(),
            review.getContent(),
            serviceAddress);
  }

  @Override
  public void deleteReviews(int productId) {
    log.debug(
        "deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }

  private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publishSupplier){
    return Flux.defer(publishSupplier).subscribeOn(scheduler);
  }
}
