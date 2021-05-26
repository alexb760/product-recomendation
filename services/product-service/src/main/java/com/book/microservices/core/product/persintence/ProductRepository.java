package com.book.microservices.core.product.persintence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Alexander Bravo
 */
public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, String> {
 Optional<ProductEntity> findByProductId(int productId);
}
