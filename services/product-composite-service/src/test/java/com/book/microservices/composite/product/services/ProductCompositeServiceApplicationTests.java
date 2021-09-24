package com.book.microservices.composite.product.services;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.book.api.core.product.Product;
import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.review.Review;
import com.book.microservices.composite.product.ProductCompositeServiceApplication;
import com.book.microservices.composite.product.TestSecurityConfig;
import com.book.util.exception.InvalidInputException;
import com.book.util.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** @author Alexander Bravo */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {ProductCompositeServiceApplication.class, TestSecurityConfig.class },
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.mongodb.port: 0",
        "eureka.client.enabled=false",
         "spring.cloud.config.enabled=false"})
@TestPropertySource("classpath:/test.properties")
public class ProductCompositeServiceApplicationTests {

  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  @Autowired private WebTestClient client;
  //  @Autowired private MockMvc mvc;

  @MockBean private ProductCompositeIntegration compositeIntegration;

  @BeforeEach
  public void setUp() {

    // We go for Junit 5 instead.
    given(compositeIntegration.getProduct(PRODUCT_ID_OK))
        .willReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));
    given(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
        .willReturn(Flux.fromIterable(
            singletonList(
                new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));
    given(compositeIntegration.getReviews(PRODUCT_ID_OK))
        .willReturn(Flux.fromIterable(
            singletonList(
                new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

    given(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
        .willThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
    given(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
        .willThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  public void contextLoads() throws Exception {}

  @Test
  public void getProductById() {

    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_OK)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.productId")
        .isEqualTo(PRODUCT_ID_OK)
        .jsonPath("$.recommendations.length()")
        .isEqualTo(1)
        .jsonPath("$.reviews.length()")
        .isEqualTo(1);
  }

  @Test
  public void getProductNotFound() {

    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .jsonPath("$.message")
        .isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
  }

  @Test
  public void getProductInvalidInput() {

    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_INVALID)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(UNPROCESSABLE_ENTITY)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
        .jsonPath("$.message")
        .isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
  }
}
