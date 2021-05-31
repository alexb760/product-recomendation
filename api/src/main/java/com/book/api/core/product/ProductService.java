package com.book.api.core.product;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * @author Alexander Bravo
 */
public interface ProductService {

 /**
  * Sample usage: curl $HOST:$PORT/product/1
  *
  * @param productId Product identifier
  * @return object representation of {@link Product}
  */
 @RequestMapping(value = "/productFake/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
 Product getProductFake(@PathVariable int productId);

 /**
  * Sample usage:
  *
  * curl -X POST $HOST:$PORT/product \
  *   -H "Content-Type: application/json" --data \
  *   '{"productId":123,"name":"product 123","weight":123}'
  *
  * @param body product object
  * @return Created product
  */
 @PostMapping(
     value    = "/product",
     consumes = "application/json",
     produces = "application/json")
 Product createProduct(@RequestBody Product body);

 /**
  * Sample usage: curl $HOST:$PORT/product/1
  *
  * @param productId Product identifier
  * @return the product, if found, else null
  */
 @GetMapping(
     value    = "/product/{productId}",
     produces = "application/json")
 Mono<Product> getProduct(@PathVariable int productId);

 /**
  * Sample usage:
  *
  * curl -X DELETE $HOST:$PORT/product/1
  *
  * @param productId Product identifier
  */
 @DeleteMapping(value = "/product/{productId}")
 void deleteProduct(@PathVariable int productId);
}
