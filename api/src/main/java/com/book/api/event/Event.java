/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.api.event;

import static java.time.LocalDateTime.now;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * An event is, for the scope of this project, defined by the following:
 *
 * <p>The {@code type} of event, for example, create or delete an event
 * <p>A   {@code key}, that identifies the data, for example, a product ID
 * <p>A   {@code data} element, that is, the actual data in the event
 * <p>A   {@code timestamp}, which describes when the event occurred
 *
 * @author Alexander Bravo
 */
public class Event<K, T> {
 public enum Type {CREATE, DELETE}

 private final Event.Type eventType;
 private final K key;
 private final T data;
// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
 private LocalDateTime eventCreatedAt;

 public Event() {
  this.eventType = null;
  this.key = null;
  this.data = null;
  this.eventCreatedAt = null;
 }

 public Event(Type eventType, K key, T data) {
  this.eventType = eventType;
  this.key = key;
  this.data = data;
  this.eventCreatedAt = now();
 }
 public Type getEventType() {
  return eventType;
 }

 public K getKey() {
  return key;
 }

 public T getData() {
  return data;
 }

 public LocalDateTime getEventCreatedAt() {
  return eventCreatedAt;
 }
}
