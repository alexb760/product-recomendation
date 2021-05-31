package com.book.microservices.core.recommendation;

import com.book.microservices.core.recommendation.persistence.RecommendationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@Slf4j
@SpringBootApplication
@ComponentScan("com.book")
public class RecommendationServiceApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(RecommendationServiceApplication.class, args);
    String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
    String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
    log.info("Connected to MongoDb: {}:{}", mongodDbHost, mongodDbPort);
  }

  @Autowired
  ReactiveMongoOperations mongoTemplate;

  @EventListener(ContextRefreshedEvent.class)
  public void initIndicesAfterStartup() {

    MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext =
        mongoTemplate.getConverter().getMappingContext();
    IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

    ReactiveIndexOperations indexOps = mongoTemplate.indexOps(RecommendationEntity.class);
    resolver.resolveIndexFor(RecommendationEntity.class).forEach(indexOps::ensureIndex);
  }
}
