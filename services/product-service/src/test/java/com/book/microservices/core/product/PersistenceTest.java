package com.book.microservices.core.product;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;

import com.book.microservices.core.product.persintence.ProductEntity;
import com.book.microservices.core.product.persintence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

/**
 * @author Alexander Bravo
 */
@DataMongoTest(properties = "spring.cloud.config.enabled=false")
@ExtendWith({SpringExtension.class})
public class PersistenceTest {

 @Autowired
 private ProductRepository repository;
 private ProductEntity savedEntity;

 @BeforeEach
 public void setupDB(){
  StepVerifier.create(repository.deleteAll()).verifyComplete();
  ProductEntity productEntity = new ProductEntity(1, "n", 2);
  StepVerifier.create(repository.save(productEntity))
      .expectNextMatches(created -> {
       savedEntity = created;
       return areProductEqual(productEntity, savedEntity);
      })
  .verifyComplete();
 }


 @Test
 public void create() {
  ProductEntity newEntity = new ProductEntity(2, "n", 2);
  StepVerifier.create(repository.save(newEntity))
      .expectNextMatches(saved -> saved.getProductId() == newEntity.getProductId())
      .verifyComplete();

  StepVerifier.create(repository.findById(newEntity.getId()))
      .expectNextMatches(faund -> areProductEqual(newEntity, faund))
      .verifyComplete();
 }

 @Test
 public void update() {
  savedEntity.setName("n2");
  StepVerifier.create(repository.save(savedEntity))
      .expectNextMatches(saved -> "n2".equals(saved.getName()))
      .verifyComplete();

  ProductEntity foundEntity = repository.findById(savedEntity.getId()).block();
  assertThat(1).isEqualTo(foundEntity.getVersion());
  assertThat("n2").isEqualTo(foundEntity.getName());
 }

 @Test
 public void delete() {
  StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
  StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
 }

 @Test
 public void getByProductId() {
  StepVerifier.create(repository.findByProductId(savedEntity.getProductId()))
      .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
      .verifyComplete();
 }

  @Disabled(value = "WIP - it does allow duplicate -Investigate")
  @Test
  public void duplicateError() {
   ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
   StepVerifier.create(repository.save(savedEntity)).expectError(DuplicateKeyException.class).verify();
  }

 @Test
 public void optimisticLockError() {

  // Store the saved entity in two separate entity objects
  ProductEntity entity1 = repository.findById(savedEntity.getId()).block();
  ProductEntity entity2 = repository.findById(savedEntity.getId()).block();

  // Update the entity using the first entity object
  entity1.setName("n1");
  repository.save(entity1).block();

  //  Update the entity using the second entity object.
  // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
  entity2.setName("n2");
  StepVerifier
      .create(repository.save(entity2))
      .expectError(OptimisticLockingFailureException.class)
      .verify();

  // Get the updated entity from the database and verify its new sate
  StepVerifier
      .create(repository.findById(savedEntity.getId()))
      .expectNextMatches(found -> 1 == found.getVersion() && "n1".equals(found.getName()))
      .verifyComplete();
 }

// private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
//  assertThat(expectedEntity.getId()).isEqualTo( actualEntity.getId());
//  assertThat(expectedEntity.getVersion()).isEqualTo( actualEntity.getVersion());
//  assertThat(expectedEntity.getProductId()).isEqualTo( actualEntity.getProductId());
//  assertThat(expectedEntity.getName()).isEqualTo( actualEntity.getName());
//  assertThat(expectedEntity.getWeight()).isEqualTo( actualEntity.getWeight());
// }

  private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
    return (expectedEntity.getId().equals(actualEntity.getId()))
        && (expectedEntity.getVersion().equals(actualEntity.getVersion()))
        && (expectedEntity.getProductId() == actualEntity.getProductId())
        && (expectedEntity.getName().equals(actualEntity.getName()))
        && (expectedEntity.getWeight() == actualEntity.getWeight());
  }
}

