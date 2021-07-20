package com.book.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@ComponentScan("com.book")
public class ProductCompositeServiceApplication {

  /**
   * TODO
   * Implement swagger
   */
//  @Value("${api.common.version}")           String apiVersion;
//  @Value("${api.common.title}")             String apiTitle;
//  @Value("${api.common.description}")       String apiDescription;
//  @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
//  @Value("${api.common.license}")           String apiLicense;
//  @Value("${api.common.licenseUrl}")        String apiLicenseUrl;
//  @Value("${api.common.contact.name}")      String apiContactName;
//  @Value("${api.common.contact.url}")       String apiContactUrl;
//  @Value("${api.common.contact.email}")     String apiContactEmail;
//
//  /**
//   * Will exposed on $HOST:$PORT/swagger-ui.html
//   *
//   * @return
//   */
//  @Bean
//  public Docket apiDocumentation() {
//
//    return new Docket(SWAGGER_2)
//        .select()
//        .apis(basePackage("se.magnus.microservices.composite.product"))
//        .paths(PathSelectors.any())
//        .build()
//        .globalResponseMessage(POST, emptyList())
//        .globalResponseMessage(GET, emptyList())
//        .globalResponseMessage(DELETE, emptyList())
//        .apiInfo(new ApiInfo(
//            apiTitle,
//            apiDescription,
//            apiVersion,
//            apiTermsOfServiceUrl,
//            new Contact(apiContactName, apiContactUrl, apiContactEmail),
//            apiLicense,
//            apiLicenseUrl,
//            emptyList()
//        ));
//  }

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
  //    ReactiveHealthIndicatorRegistry registry = new DefaultReactiveHealthIndicatorRegistry(new
  // LinkedHashMap<>());
  //
  //    registry.register("product", () -> integration.getProductHealth());
  //    registry.register("recommendation", () -> integration.getRecommendationHealth());
  //    registry.register("review", () -> integration.getReviewHealth());
  //
  //    return new CompositeReactiveHealthIndicator(healthAggregator, registry);
  //  }

  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    final WebClient.Builder builder = WebClient.builder();
    return builder;
  }
}
