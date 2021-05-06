package com.book.microservices.composite.product.services;

import static org.springframework.http.HttpMethod.GET;
import com.book.api.core.product.Product;
import com.book.api.core.product.ProductService;
import com.book.api.core.recomendation.Recomendation;
import com.book.api.core.recomendation.RecommendationService;
import com.book.api.core.review.Review;
import com.book.api.core.review.ReviewService;
import com.book.util.exception.InvalidInputException;
import com.book.util.exception.NotFoundException;
import com.book.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** @author Alexander Bravo */
@Slf4j
@Component
public class ProductCompositeIntegration
    implements ProductService, RecommendationService, ReviewService {

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
      RestTemplate restTemplate,
      ObjectMapper mapper,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.recommendation-service.host}") String recommendationServiceHost,
      @Value("${app.recommendation-service.port}") int recommendationServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {

    this.restTemplate = restTemplate;
    this.mapper = mapper;

    // "http://" + productServiceHost + ":" + productServicePort + "/product/";
    productServiceUrl = getFormattedURL(productServiceHost, productServicePort, "product/");
    recommendationServiceUrl =
        getFormattedURL(
            recommendationServiceHost, recommendationServicePort, "recommendation?productId=");
    reviewServiceUrl = getFormattedURL(reviewServiceHost, reviewServicePort, "/review?productId=");
  }

  private String getFormattedURL(String hostName, int servicePort, String pathAPI) {
    return String.format("http://%s:%s/%s", hostName, servicePort, pathAPI);
  }

  @Override
  public Product getProduct(int productId) {
    try {
      String url = productServiceUrl + productId;
      log.debug("Will call getProduct API on URL: {}", url);

      Product product = restTemplate.getForObject(url, Product.class);
      log.debug("Found a product with id: {}", product.getProductId());

      return product;

    } catch (HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
            default -> {
                log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                log.warn("Error body: {}", ex.getResponseBodyAsString());
                throw ex;
            }
        }
    }
  }

  private String getErrorMessage(HttpClientErrorException ex) {
    try {
        return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
        return ex.getMessage();
    }
    }

  @Override
  public List<Recomendation> getRecommendations(int productId) {
      try {
          String url = recommendationServiceUrl + productId;
          log.debug("Will call getRecommendations API on URL: {}", url);
          List<Recomendation> recommendations =
              restTemplate
                  .exchange(url, GET, null, new ParameterizedTypeReference<List<Recomendation>>() {})
                  .getBody();

          log.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
          return recommendations;

      } catch (Exception ex) {
          log.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
          return List.of();
      }
  }

  @Override
  public List<Review> getReviews(int productId) {
      try {
          String url = reviewServiceUrl + productId;

          log.debug("Will call getReviews API on URL: {}", url);
          List<Review> reviews =
              restTemplate
                  .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {})
                  .getBody();

          log.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
          return reviews;

      } catch (Exception ex) {
          log.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
          return List.of();
      }
  }
}