package com.book.micorservices.authorizationserver.configuration;

import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

/**
 * An instance of Legacy Authorization Server (spring-security-oauth2) that uses a single,
 * not-rotating key and exposes a JWK endpoint.
 *
 * <p>See <a target="_blank"
 * href="https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/reference/htmlsingle/">
 * Spring Security OAuth Autoconfig's documentation</a> for additional detail
 *
 * @author Alexander Bravo
 */
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {}
