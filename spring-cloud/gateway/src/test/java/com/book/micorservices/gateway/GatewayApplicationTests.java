package com.book.micorservices.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=.",
      "eureka.client.enabled=false",
      "spring.cloud.config.enabled=false"
    })
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
