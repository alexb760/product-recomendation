package com.book.microservices.composite.product;

import com.book.microservices.composite.product.services.ProductCompositeIntegration;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.book")
public class ProductCompositeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductCompositeServiceApplication.class, args);
  }

  @Bean
  RestTemplate setRestTemplate(){
  	return new RestTemplate();
  }

//  @Autowired
//  HealthAggregator healthAggregator;
//
//  @Autowired
//  ProductCompositeIntegration integration;
//
//  @Bean
//  ReactiveHealthIndicator coreServices() {
//
//    ReactiveHealthIndicatorRegistry registry = new DefaultReactiveHealthIndicatorRegistry(new LinkedHashMap<>());
//
//    registry.register("product", () -> integration.getProductHealth());
//    registry.register("recommendation", () -> integration.getRecommendationHealth());
//    registry.register("review", () -> integration.getReviewHealth());
//
//    return new CompositeReactiveHealthIndicator(healthAggregator, registry);
//  }

}
