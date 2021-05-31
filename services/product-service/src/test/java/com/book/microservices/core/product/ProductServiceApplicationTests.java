package com.book.microservices.core.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.book.api.core.product.Product;
import com.book.microservices.core.product.persintence.ProductRepository;
import com.book.microservices.core.product.services.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"spring.data.mongodb.port: 0"})
class ProductServiceApplicationTests {

  @Autowired private WebTestClient client;

  @Autowired private ProductRepository repository;
  @Autowired private ProductServiceImpl productService;

  @BeforeEach
  public void setupDb() {
    repository.deleteAll().block();
  }

  @Disabled
  @Test
  public void getProductById() {

    int productId = 1;

    assertNull(repository.findByProductId(productId).block());
    assertEquals(0, repository.count().block());

//    postAndVerifyProduct(productId, OK);
    Product product = new Product(productId, "Name " + productId, productId, "SA");
    productService.createProduct(product);

    assertNotNull(repository.findByProductId(productId).block());
    assertEquals(1, repository.count().block());

    getAndVerifyProduct(productId, OK).jsonPath("$.productId").isEqualTo(productId);
  }

  @Disabled(value = "WIP - Will work when event-driven is implemented")
  @Test
  public void duplicateError() {

    int productId = 1;

//    postAndVerifyProduct(productId, OK);
    Product product = new Product(productId, "Name " + productId, productId, "SA");
    productService.createProduct(product);
    assertNotNull(repository.findByProductId(productId).block());

    postAndVerifyProduct(productId, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/product")
        .jsonPath("$.message")
        .isEqualTo("Duplicate key, Product Id: " + productId);
  }

  @Disabled(value = "WIP - Will work when event-driven is implemented")
  @Test
  public void deleteProduct() {

    int productId = 1;

//    postAndVerifyProduct(productId, OK);
    Product product = new Product(productId, "Name " + productId, productId, "SA");
    productService.createProduct(product);
    assertNotNull(repository.findByProductId(productId).block());

    deleteAndVerifyProduct(productId, OK);
    assertNull(repository.findByProductId(productId).block());

    deleteAndVerifyProduct(productId, OK);
  }

  @Test
  public void getProductInvalidParameterString() {

    getAndVerifyProduct("/no-integer", BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/product/no-integer")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  public void getProductNotFound() {

    int productIdNotFound = 13;
    getAndVerifyProduct(productIdNotFound, NOT_FOUND)
        .jsonPath("$.path")
        .isEqualTo("/product/" + productIdNotFound)
        .jsonPath("$.message")
        .isEqualTo("No product found for productId: " + productIdNotFound);
  }

  @Test
  public void getProductInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/product/" + productIdInvalid)
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyProduct("/" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      String productIdPath, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/product" + productIdPath)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    Product product = new Product(productId, "Name " + productId, productId, "SA");
    return client
        .post()
        .uri("/product")
        .body(just(product), Product.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return client
        .delete()
        .uri("/product/" + productId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectBody();
  }

  //  private void sendCreateProductEvent(int productId) {
  //    Product product = new Product(productId, "Name " + productId, productId, "SA");
  //    Event<Integer, Product> event = new Event(CREATE, productId, product);
  //    input.send(new GenericMessage<>(event));
  //  }
  //
  //  private void sendDeleteProductEvent(int productId) {
  //    Event<Integer, Product> event = new Event(DELETE, productId, null);
  //    input.send(new GenericMessage<>(event));
  //  }
}
