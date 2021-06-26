package com.book.microservices.composite.product;

import com.book.api.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/** @author Alexander Bravo */
@Slf4j
public final class IsSameEvent extends TypeSafeMatcher<String> {
  private final ObjectMapper mapper;
  private final Event expectedEvent;

  private IsSameEvent(Event expectedEvent) {
    this.expectedEvent = expectedEvent;
    mapper = new ObjectMapper();
    mapper.registerModule(new JSR310Module());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  protected boolean matchesSafely(String eventAsJson) {

    if (expectedEvent == null) return false;

    log.trace("Convert the following json string to a map: {}", eventAsJson);
    Map mapEvent = convertJsonStringToMap(eventAsJson);
    mapEvent.remove("eventCreatedAt");

    Map mapExpectedEvent = getMapWithoutCreatedAt(expectedEvent);

    log.trace("Got the map: {}", mapEvent);
    log.trace("Compare to the expected map: {}", mapExpectedEvent);
    return mapEvent.equals(mapExpectedEvent);
  }

  @Override
  public void describeTo(Description description) {
    String expectedJson = convertObjectToJsonString(expectedEvent);
    description.appendText("expected to look like " + expectedJson);
  }

  public static Matcher<String> sameEventExceptCreatedAt(Event expectedEvent) {
    return new IsSameEvent(expectedEvent);
  }

  private Map getMapWithoutCreatedAt(Event event) {
    Map mapEvent = convertObjectToMap(event);
    mapEvent.remove("eventCreatedAt");
    return mapEvent;
  }

  private Map convertObjectToMap(Object object) {
    JsonNode node = mapper.convertValue(object, JsonNode.class);
    return mapper.convertValue(node, Map.class);
  }

  private String convertObjectToJsonString(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Map convertJsonStringToMap(String eventAsJson) {
    try {
      return mapper.readValue(eventAsJson, new TypeReference<HashMap>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
