package com.book.microservices.core.product;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.data.domain.Sort.Direction.ASC;

import com.book.microservices.core.product.persintence.ProductEntity;
import com.book.microservices.core.product.persintence.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Alexander Bravo
 */
@DataMongoTest()
@ExtendWith({SpringExtension.class})
public class PersistenceTest {

 @Autowired
 private ProductRepository repository;
 private ProductEntity savedEntity;

 @BeforeEach
 public void setupDB(){
  repository.deleteAll();
  ProductEntity productEntity = new ProductEntity(1, "n", 2);
  savedEntity = repository.save(productEntity);
  assertThat(savedEntity).isEqualTo(productEntity);
 }


 @Test
 public void create() {

  ProductEntity newEntity = new ProductEntity(2, "n", 2);
  repository.save(newEntity);

  ProductEntity foundEntity = repository.findById(newEntity.getId()).get();
  assertEqualsProduct(newEntity, foundEntity);
  assertThat(2).isEqualTo(repository.count());
 }

 @Test
 public void update() {
  savedEntity.setName("n2");
  repository.save(savedEntity);

  ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();
  assertThat(1).isEqualTo(foundEntity.getVersion());
  assertThat("n2").isEqualTo(foundEntity.getName());
 }

 @Test
 public void delete() {
  repository.delete(savedEntity);
  assertThat(repository.existsById(savedEntity.getId())).isFalse();
 }

 @Test
 public void getByProductId() {
  Optional<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());

  assertThat(entity.isPresent()).isTrue();
  assertEqualsProduct(savedEntity, entity.get());
 }

  @Test
  public void duplicateError() {
    ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
    Assertions.assertThrows(
        DuplicateKeyException.class,
        () -> {
          repository.save(entity);
        });
  }

 @Test
 public void optimisticLockError() {

  // Store the saved entity in two separate entity objects
  ProductEntity entity1 = repository.findById(savedEntity.getId()).get();
  ProductEntity entity2 = repository.findById(savedEntity.getId()).get();

  // Update the entity using the first entity object
  entity1.setName("n1");
  repository.save(entity1);

  //  Update the entity using the second entity object.
  // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
  try {
   entity2.setName("n2");
   repository.save(entity2);

   fail("Expected an OptimisticLockingFailureException");
  } catch (OptimisticLockingFailureException e) {}

  // Get the updated entity from the database and verify its new sate
  ProductEntity updatedEntity = repository.findById(savedEntity.getId()).get();
  assertThat(1).isEqualTo(updatedEntity.getVersion());
  assertThat("n1").isEqualTo(updatedEntity.getName());
 }

 @Test
 public void paging() {

  repository.deleteAll();

  List<ProductEntity> newProducts = rangeClosed(1001, 1010)
      .mapToObj(i -> new ProductEntity(i, "name " + i, i))
      .collect(Collectors.toList());
  repository.saveAll(newProducts);

  Pageable nextPage = PageRequest.of(0, 4, ASC, "productId");
  nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
  nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
  nextPage = testNextPage(nextPage, "[1009, 1010]", false);
 }

  private Pageable testNextPage(
      Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
    Page<ProductEntity> productPage = repository.findAll(nextPage);
    assertThat(expectedProductIds)
        .isEqualTo(
            productPage.getContent().stream()
                .map(p -> p.getProductId())
                .collect(Collectors.toList())
                .toString());
    assertThat(expectsNextPage).isEqualTo(productPage.hasNext());
    return productPage.nextPageable();
  }

 private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
  assertThat(expectedEntity.getId()).isEqualTo( actualEntity.getId());
  assertThat(expectedEntity.getVersion()).isEqualTo( actualEntity.getVersion());
  assertThat(expectedEntity.getProductId()).isEqualTo( actualEntity.getProductId());
  assertThat(expectedEntity.getName()).isEqualTo( actualEntity.getName());
  assertThat(expectedEntity.getWeight()).isEqualTo( actualEntity.getWeight());
 }
}

