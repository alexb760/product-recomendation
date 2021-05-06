package com.book.api.composite.product;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Alexander Bravo */
public interface ProductCompositeService {

  /**
   * Sample usage: curl $HOST:$PORT/product-composite/1
   *
   * @param productId product identifier
   * @return the composite product info, if found, else null
   */
  @GetMapping(value = "/product-composite/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ProductAggregate getProduct(@PathVariable int productId);
}
