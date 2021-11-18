package com.book.microservices.core.product.services;

import com.book.api.core.product.Product;
import com.book.api.core.product.ProductService;
import com.book.microservices.core.product.persintence.ProductEntity;
import com.book.microservices.core.product.persintence.ProductRepository;
import com.book.util.exception.InvalidInputException;
import com.book.util.exception.NotFoundException;
import com.book.util.http.ServiceUtil;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Alexander Bravo
 */
@Slf4j
@RestController
public class ProductServiceImpl implements ProductService {

 private final ServiceUtil serviceUtil;
 private final ProductRepository productRepository;
 private final ProductMapper productMapper;

  @Autowired
  public ProductServiceImpl(
      ServiceUtil serviceUtil,
      ProductRepository productRepository,
      ProductMapper productMapper) {
    this.serviceUtil = serviceUtil;
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Override
  public Product createProduct(Product body) {
    if (body.getProductId() < 1)
      throw new InvalidInputException("Invalid productId: " + body.getProductId());

    ProductEntity entity = productMapper.apiToEntity(body);
    Mono<Product> newEntity =
        productRepository
            .save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->
                    new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
            .map(productMapper::entityToApi);

    return newEntity.block();
  }

  @Override
  public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
      if (delay > 0) simulateDelay(delay);
      if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);
    // Sync API
    //    ProductEntity entity = productRepository.findByProductId(productId)
    //            .orElseThrow(() ->
    //                    new NotFoundException(
    //                        String.format("No product found for productId: %s", productId)));
    //
    //    Product response = productMapper.entityToApi(entity);
    //    log.debug("/product: return the found product for productId={}", productId);

    // Async Api
    return productRepository
        .findByProductId(productId)
        .switchIfEmpty(
            Mono.error( new NotFoundException(
                    String.format("No product found for productId: %s", productId))))
        .log()
        .map(productMapper::entityToApi)
        .map(entity -> {
              entity.setServiceAddress(serviceUtil.getServiceAddress());
              return entity;
            });
  }

 @Override
 public Product getProductFake(int productId) {
  log.debug("/product return the found product for productId={}", productId);

  if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

  if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);
  return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
 }

 @Override
 public void deleteProduct(int productId) {
     if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
     log.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
//    productRepository.findByProductId(productId).ifPresent(productRepository::delete);
     productRepository
         .findByProductId(productId)
         .log()
         .map(productRepository::delete)
         .flatMap(e -> e)
         .block();
 }

  private void simulateDelay(int delay) {
    log.debug("Sleeping for {} seconds...", delay);
    try {
      Thread.sleep(delay * 1000L);
    } catch (InterruptedException e) {
    }
    log.debug("Moving on...");
  }

  private void throwErrorIfBadLuck(int faultPercent) {
    int randomThreshold = getRandomNumber(1, 100);
    if (faultPercent < randomThreshold) {
      log.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
    } else {
      log.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
      throw new RuntimeException("Something went wrong...");
    }
  }

  private final Random randomNumberGenerator = new Random();

  private int getRandomNumber(int min, int max) {

    if (max < min) {
      throw new RuntimeException("Max must be greater than min");
    }

    return randomNumberGenerator.nextInt((max - min) + 1) + min;
  }
}
