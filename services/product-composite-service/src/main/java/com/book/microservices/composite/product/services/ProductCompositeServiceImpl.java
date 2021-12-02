package com.book.microservices.composite.product.services;

import com.book.api.composite.product.ProductAggregate;
import com.book.api.composite.product.ProductCompositeService;
import com.book.api.composite.product.RecommendationSummary;
import com.book.api.composite.product.ReviewSummary;
import com.book.api.composite.product.ServiceAddresses;
import com.book.api.core.product.Product;
import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.review.Review;
import com.book.util.exception.NotFoundException;
import com.book.util.http.ServiceUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** @author Alexander Bravo */
@Slf4j
@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

//  private final SecurityContext nullSC = new SecurityContextImpl();

  private final ServiceUtil serviceUtil;
  private ProductCompositeIntegration integration;

  @Autowired
  public ProductCompositeServiceImpl(
      ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public void createCompositeProduct(ProductAggregate body) {
    try {
      log.debug(
          "createCompositeProduct: creates a new composite entity for productId: {}",
          body.getProductId());

      Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(product);

      if (body.getRecommendations() != null) {
        body.getRecommendations()
            .forEach(r -> {
                  Recommendation recommendation =
                      new Recommendation(
                          body.getProductId(),
                          r.getRecommendationId(),
                          r.getAuthor(),
                          r.getRate(),
                          r.getContent(),
                          null);
                  integration.createRecommendation(recommendation);
            });
      }

      if (body.getReviews() != null) {
        body.getReviews()
            .forEach(r -> {
                  Review review =
                      new Review(
                          body.getProductId(),
                          r.getReviewId(),
                          r.getAuthor(),
                          r.getSubject(),
                          r.getContent(),
                          null);
                  integration.createReview(review);
            });
      }

      log.debug(
          "createCompositeProduct: composite entities created for productId: {}",
          body.getProductId());

    } catch (RuntimeException re) {
      log.warn("createCompositeProduct failed", re);
      throw re;
    }
  }

  @Override
  public Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent) {
     // --- Blocking API refer to git tag v1.0.0-cap6

    // --- Non-Blocking code ---//
    return Mono.zip(
        values -> createProductAggregate(
            (Product) values[0],
            (List<Recommendation>) values[1],
            (List<Review>) values[2],
            serviceUtil.getServiceAddress()),
//            ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
            integration.getProduct(productId, delay, faultPercent)
                .onErrorReturn(CallNotPermittedException.class, getProductFallbackValue(productId)),
        integration.getRecommendations(productId).collectList(),
        integration.getReviews(productId).collectList())
        .doOnError(ex -> log.warn("getCompositeProduct failed: {}", ex.toString()))
        .log();
  }

  private Product getProductFallbackValue(int productId) {

    log.warn("Creating a fallback product for productId = {}", productId);

    if (productId == 13) {
      String errMsg = "Product Id: " + productId + " not found in fallback cache!";
      log.warn(errMsg);
      throw new NotFoundException(errMsg);
    }

    return new Product(
        productId, "Fallback product" + productId, productId, serviceUtil.getServiceAddress());
  }

    @Override
  public void deleteCompositeProduct(int productId) {
    try
    {
      log.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

      integration.deleteProduct(productId);
      integration.deleteRecommendations(productId);
      integration.deleteReviews(productId);

      log.debug("getCompositeProduct: aggregate entities deleted for productId: {}", productId);
    } catch (RuntimeException e) {
      log.warn("deleteCompositeProduct failed: {}", e.toString());
      throw e;
    }
  }

  private ProductAggregate createProductAggregate(
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {

    // 1. Setup product info
    int productId = product.getProductId();
    String name = product.getName();
    int weight = product.getWeight();

    // 2. Copy summary recommendation info, if available
    List<RecommendationSummary> recommendationSummaries =
        (recommendations == null)
            ? null
            : recommendations.stream()
                .map(
                    r ->
                        new RecommendationSummary(
                            r.getRecommendationId(), r.getAuthor(), r.getContent(), r.getRate()))
                .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummary> reviewSummaries =
        (reviews == null)
            ? null
            : reviews.stream()
                .map(
                    r ->
                        new ReviewSummary(
                            r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = product.getServiceAddress();
    String reviewAddress =
        (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
    String recommendationAddress =
        (recommendations != null && recommendations.size() > 0)
            ? recommendations.get(0).getServiceAddress()
            : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregate(
        productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
  }
}
