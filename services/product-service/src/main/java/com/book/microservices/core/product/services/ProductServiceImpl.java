package com.book.microservices.core.product.services;

import com.book.api.core.product.Product;
import com.book.api.core.product.ProductService;
import com.book.util.exception.InvalidInputException;
import com.book.util.exception.NotFoundException;
import com.book.util.http.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Bravo
 */
@Slf4j
@RestController
public class ProductServiceImpl implements ProductService {

 private final ServiceUtil serviceUtil;

 @Autowired
 public ProductServiceImpl(ServiceUtil serviceUtil) {
  this.serviceUtil = serviceUtil;
 }

 @Override
 public Product getProduct(int productId) {
  log.debug("/product return the found product for productId={}", productId);

  if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

  if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);
  return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
 }
}
