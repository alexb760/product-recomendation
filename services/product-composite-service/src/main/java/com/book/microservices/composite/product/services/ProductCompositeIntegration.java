package com.book.microservices.composite.product.services;

import static com.book.api.event.Event.Type.CREATE;
import static com.book.api.event.Event.Type.DELETE;

import com.book.api.core.product.Product;
import com.book.api.core.product.ProductService;
import com.book.api.core.recomendation.Recommendation;
import com.book.api.core.recomendation.RecommendationService;
import com.book.api.core.review.Review;
import com.book.api.core.review.ReviewService;
import com.book.api.event.Event;
import com.book.util.exception.InvalidInputException;
import com.book.util.exception.NotFoundException;
import com.book.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** @author Alexander Bravo */
@Slf4j
@EnableBinding(ProductCompositeIntegration.MessageSources.class)
@Component
@Getter
public class ProductCompositeIntegration
    implements ProductService, RecommendationService, ReviewService {

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl = "http://product";
    private final String recommendationServiceUrl = "http://recommendation";
    private final String reviewServiceUrl = "http://review";
    private final int productServiceTimeoutSec;

    //Event Driver declarations
    private MessageSources messageSources;

    //This interface is used to declare the needed topic to bind to
    public interface MessageSources {

        String OUTPUT_PRODUCTS = "output-products";
        String OUTPUT_RECOMMENDATIONS = "output-recommendations";
        String OUTPUT_REVIEWS = "output-reviews";

        @Output(OUTPUT_PRODUCTS)
        MessageChannel outputProducts();

        @Output(OUTPUT_RECOMMENDATIONS)
        MessageChannel outputRecommendations();

        @Output(OUTPUT_REVIEWS)
        MessageChannel outputReviews();
    }

    @Autowired
    public ProductCompositeIntegration(
        WebClient.Builder webClient,
        RestTemplate restTemplate,
        ObjectMapper mapper,
        MessageSources messageSources,
        @Value("${app.product-service.timeoutSec}") int productServiceTimeoutSec) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.messageSources = messageSources;

        this.webClientBuilder = webClient;
        this.productServiceTimeoutSec = productServiceTimeoutSec;
    }

    @Override
    public Product getProductFake(int productId) {
        try {
            String url = productServiceUrl + "/product/" + productId;
            log.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            log.debug("Found a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
                default -> {
                    log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    log.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
                }
            }
        }
    }

    @Override
    public Product createProduct(Product body) {
        messageSources.outputProducts()
            .send(MessageBuilder
                .withPayload(new Event<>(CREATE, body.getProductId(), body)).build());
        return body;
    }

    @Override
    public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
//        String url = productServiceUrl + "/product/" + productId;
//        return getWebClient()
//            .get()
//            .uri(url)
//            .retrieve()
//            .bodyToMono(Product.class)
//            .log()
//            .onErrorMap(WebClientResponseException.class, this::handleException);
        log.info("Testing new delay only as a test mode.");
        URI url = UriComponentsBuilder
            .fromUriString(productServiceUrl +
                "/product/{productId}?delay={delay}&faultPercent={faultPercent}")
            .build(productId, delay, faultPercent);

        log.debug("Will call the getProduct API on URL: {}", url);

        return getWebClient().get().uri(url)
            .retrieve().bodyToMono(Product.class).log()
            .onErrorMap(WebClientResponseException.class, ex -> handleException(ex))
            .timeout(Duration.ofSeconds(productServiceTimeoutSec));
    }

    @Override
    public void deleteProduct(int productId) {
        messageSources.outputProducts()
            .send(MessageBuilder.withPayload(new Event<>(DELETE, productId, null))
                .build());
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        messageSources.outputRecommendations()
            .send(MessageBuilder.withPayload(new Event<>(CREATE, body.getProductId(), body)).build());
        return body;
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        String url = recommendationServiceUrl + "/recommendation?productId=" + productId;
        log.debug("Will call getRecommendations API on URL: {}", url);

        return getWebClient().get().uri(url)
            .retrieve()
            .bodyToFlux(Recommendation.class)
            .log()
            .onErrorResume(error -> Flux.empty());
    }

    @Override
    public void deleteRecommendations(int productId) {
        messageSources.outputRecommendations()
            .send(MessageBuilder.withPayload(new Event<>(DELETE, productId, null)).build());
    }

    @Override
    public Review createReview(Review body) {
        messageSources.outputReviews()
            .send(MessageBuilder.withPayload(new Event<>(CREATE, body.getProductId(), body)).build());
        return body;
    }

    @Override
    public Flux<Review> getReviews(int productId) {
        String url = reviewServiceUrl + "/review?productId=" + productId;

        log.debug("Will call getReviews API on URL: {}", url);
        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get()
            .uri(url)
            .retrieve()
            .bodyToFlux(Review.class)
            .log()
            .onErrorResume(error -> Flux.empty());
    }

    public Mono<Health> getProductHealth() {
        return getHealth(productServiceUrl);
    }

    public Mono<Health> getRecommendationHealth() {
        return getHealth(recommendationServiceUrl);
    }

    public Mono<Health> getReviewHealth() {
        return getHealth(reviewServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        log.debug("Will call the Health API on URL: {}", url);
        return getWebClient().get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log();
    }

    @Override
    public void deleteReviews(int productId) {
        messageSources.outputReviews()
            .send(MessageBuilder.withPayload(new Event<>(DELETE, productId, null)).build());
    }


    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                log.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            log.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                log.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                log.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }
}
