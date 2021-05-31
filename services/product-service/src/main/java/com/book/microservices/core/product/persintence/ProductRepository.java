package com.book.microservices.core.product.persintence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author Alexander Bravo
 */
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {
 Mono<ProductEntity> findByProductId(int productId);
}
