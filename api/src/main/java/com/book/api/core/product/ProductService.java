package com.book.api.core.product;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
 @RequestMapping(value = "/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
 Product getProduct(@PathVariable int productId);
}
