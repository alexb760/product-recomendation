package com.book.micorservices.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=.")
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
