package com.book.microservices.core.product.services;

import com.book.api.core.product.Product;
import com.book.api.core.product.ProductService;
import com.book.api.event.Event;
import com.book.util.exception.EventProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/** @author Alexander Bravo */
@Slf4j
@EnableBinding(Sink.class)
public class MessageProcessor {
  private final ProductService productService;

  @Autowired
  public MessageProcessor(ProductService productService) {
    this.productService = productService;
  }

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Product> event) {

    log.info("Process message created at {}...", event.getEventCreatedAt());

   switch (event.getEventType())
   {
    case CREATE -> {
     Product product = event.getData();
     log.info("Create product with ID: {}", product.getProductId());
     productService.createProduct(product);
    }
    case DELETE -> {
     int productId = event.getKey();
     log.info("Delete recommendations with ProductID: {}", productId);
     productService.deleteProduct(productId);
    }
    default -> {
     String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
     log.warn(errorMessage);
     throw new EventProcessingException(errorMessage);
    }
   }

    log.info("Message processing done!");
  }
}
