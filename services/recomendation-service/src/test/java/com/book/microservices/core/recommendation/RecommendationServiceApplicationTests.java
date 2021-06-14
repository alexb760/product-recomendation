package com.book.microservices.core.recommendation;

import static com.book.api.event.Event.Type.CREATE;
import static com.book.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import com.book.api.core.product.Product;
import com.book.api.core.recomendation.Recommendation;
import com.book.api.event.Event;
import com.book.microservices.core.recommendation.persistence.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"spring.data.mongodb.port: 0"})
class RecommendationServiceApplicationTests {

  @Autowired private WebTestClient client;

  @Autowired private RecommendationRepository repository;
  @Autowired private Sink chanels;

  private AbstractMessageChannel input = null;

  @BeforeEach
  public void setupDb() {
    input = (AbstractMessageChannel) chanels.input();
    repository.deleteAll().block();
  }

//  @Disabled("WIP - Ready when event-driver implemented")
  @Test
  public void getRecommendationsByProductId() {

    int productId = 1;

    sendCreateRecommendationEvent(productId, 1);
    sendCreateRecommendationEvent(productId, 2);
    sendCreateRecommendationEvent(productId, 3);

    assertEquals(3, repository.findByProductId(productId).collectList().block().size());

    getAndVerifyRecommendationsByProductId(productId, OK)
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[2].productId")
        .isEqualTo(productId)
        .jsonPath("$[2].recommendationId")
        .isEqualTo(3);
  }

  @Disabled("WIP - Ready when event-driver implemented")
  @Test
  public void duplicateError() {

    int productId = 1;
    int recommendationId = 1;

    postAndVerifyRecommendation(productId, recommendationId, OK)
        .jsonPath("$.productId")
        .isEqualTo(productId)
        .jsonPath("$.recommendationId")
        .isEqualTo(recommendationId);

    assertEquals(1, repository.count());

    postAndVerifyRecommendation(productId, recommendationId, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/recommendation")
        .jsonPath("$.message")
        .isEqualTo("Duplicate Key, producId: 1 recommendationId: 1");

    assertEquals(1, repository.count());
  }

//  @Disabled("WIP - Ready when event-driver implemented")
  @Test
  public void deleteRecommendations() {

    int productId = 1;
    int recommendationId = 1;

    sendCreateRecommendationEvent(productId, recommendationId);
    assertEquals(1, repository.findByProductId(productId).collectList().block().size());

    sendDeleteRecommendationEvent(productId);
    assertEquals(0, repository.findByProductId(productId).collectList().block().size());

    sendDeleteRecommendationEvent(productId);
  }

  @Test
  public void getRecommendationsMissingParameter() {

    getAndVerifyRecommendationsByProductId("", BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/recommendation")
        .jsonPath("$.message")
        .isEqualTo("Required int parameter 'productId' is not present");
  }

  @Disabled
  @Test
  public void getRecommendationsInvalidParameter() {

    getAndVerifyRecommendationsByProductId("?productId=no-integer", BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/recommendation")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  public void getRecommendationsNotFound() {

    getAndVerifyRecommendationsByProductId("?productId=113", OK)
        .jsonPath("$.length()")
        .isEqualTo(0);
  }

  @Test
  public void getRecommendationsInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyRecommendationsByProductId("?productId=" + productIdInvalid, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/recommendation")
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(
      String productIdQuery, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/recommendation" + productIdQuery)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyRecommendation(
      int productId, int recommendationId, HttpStatus expectedStatus) {
    Recommendation recommendation =
        new Recommendation(
            productId,
            recommendationId,
            "Author " + recommendationId,
            recommendationId,
            "Content " + recommendationId,
            "SA");
    return client
        .post()
        .uri("/recommendation")
        .body(just(recommendation), Recommendation.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

//  private WebTestClient.BodyContentSpec deleteAndVerifyRecommendationsByProductId(
//      int productId, HttpStatus expectedStatus) {
//    return client
//        .delete()
//        .uri("/recommendation?productId=" + productId)
//        .accept(APPLICATION_JSON)
//        .exchange()
//        .expectStatus()
//        .isEqualTo(expectedStatus)
//        .expectBody();
//  }

  private void sendCreateRecommendationEvent(int productId, int recommendationId) {
    Recommendation recommendation =
        new Recommendation(
            productId,
            recommendationId,
            "Author " + recommendationId,
            recommendationId,
            "Content " + recommendationId,
            "SA");
    Event<Integer, Product> event = new Event(CREATE, productId, recommendation);
    input.send(new GenericMessage<>(event));
  }

  private void sendDeleteRecommendationEvent(int productId) {
    Event<Integer, Product> event = new Event(DELETE, productId, null);
    input.send(new GenericMessage<>(event));
  }
}
