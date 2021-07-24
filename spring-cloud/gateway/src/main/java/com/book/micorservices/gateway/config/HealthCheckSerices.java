package com.book.micorservices.gateway.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** @author Alexander Bravo */
@Slf4j
@Component("CheckHealthComponent")
public class HealthCheckSerices implements CompositeReactiveHealthContributor {
  private WebClient webClient;
  private final WebClient.Builder webClientBuilder;

  private final Map<String, ReactiveHealthContributor> contrubutors = new LinkedHashMap<>();
  private final ReactiveHealthIndicator product = () -> getHealth("http://product");
  private final ReactiveHealthIndicator recommendation = () -> getHealth("http://recommendation");
  private final ReactiveHealthIndicator review = () -> getHealth("http://review");
  private final ReactiveHealthIndicator productComposite =
      () -> getHealth("http://product-composite");


  @Autowired
  public HealthCheckSerices(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
    contrubutors.put("product", product);
    contrubutors.put("recommendation", recommendation);
    contrubutors.put("review", review);
    contrubutors.put("product-composite", productComposite);
  }

  @Override
  public ReactiveHealthContributor getContributor(String name) {
    return contrubutors.get(name);
  }

  @Override
  public Iterator<NamedContributor<ReactiveHealthContributor>> iterator() {
    return contrubutors.entrySet().stream()
        .map(entry -> NamedContributor.of(entry.getKey(), entry.getValue()))
        .iterator();
  }

  private Mono<Health> getHealth(final String url) {
    String urlHealth = String.format("%s%s", url, "/actuator/health");
    log.debug("Will call the Health API on URL: {}", urlHealth);
    return getWebClient()
        .get()
        .uri(urlHealth)
        .retrieve()
        .bodyToMono(String.class)
        .map(s -> new Health.Builder().up().build())
        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
        .log();
  }

  private WebClient getWebClient() {
    if (webClient == null) {
      webClient = webClientBuilder.build();
    }
    return webClient;
  }
}
