package com.book.microservices.composite.product;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Java class to control access resources.
 *
 * @author Alexander Bravo
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {

  private final String uriSetServer;

  public SecurityConfig(
      @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String uriSetServer) {
    this.uriSetServer = uriSetServer;
  }


  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    log.info(":::: uriSetServer::::->{}", uriSetServer);
    http.authorizeExchange()
        .pathMatchers("/actuator/**")
        .permitAll()
        .pathMatchers(POST, "/product-composite/**").hasAuthority("SCOPE_product:write")
        .pathMatchers(DELETE, "/product-composite/**").hasAuthority("SCOPE_product:write")
        .pathMatchers(GET, "/product-composite/**").hasAuthority("SCOPE_product:read")
        .anyExchange()
        .authenticated()
        .and()
        .oauth2ResourceServer(
            oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt -> jwt.jwkSetUri(uriSetServer)));

    return http.build();
  }
}
