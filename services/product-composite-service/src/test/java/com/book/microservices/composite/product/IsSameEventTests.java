package com.book.microservices.composite.product;

import static com.book.api.event.Event.Type.CREATE;
import static com.book.api.event.Event.Type.DELETE;
import static com.book.microservices.composite.product.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.book.api.core.product.Product;
import com.book.api.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.Test;

/**
 * @author Alexander Bravo
 */
public class IsSameEventTests {
 private final ObjectMapper mapper;

 public IsSameEventTests(){
  mapper = new ObjectMapper();
  mapper.registerModule(new JSR310Module());
  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
 }

 @Test
 public void testEventObjectCompare() throws JsonProcessingException {

  // Event #1 and #2 are the same event, but occurs as different times
  // Event #3 and #4 are different events
  Event<Integer, Product> event1 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
  Event<Integer, Product> event2 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
  Event<Integer, Product> event3 = new Event<>(DELETE, 1, null);
  Event<Integer, Product> event4 = new Event<>(CREATE, 1, new Product(2, "name", 1, null));

  String event1JSon = mapper.writeValueAsString(event1);

  assertThat(event1JSon, is(sameEventExceptCreatedAt(event2)));
  assertThat(event1JSon, not(sameEventExceptCreatedAt(event3)));
  assertThat(event1JSon, not(sameEventExceptCreatedAt(event4)));
 }
}
