package com.book.util.http;

import java.time.ZonedDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Alexander Bravo
 */
@Getter
public class HttpErrorInfo {
 private final ZonedDateTime timestamp;
 private final String path;
 private final HttpStatus httpStatus;
 private final String message;


 public HttpErrorInfo() {
  timestamp = null;
  this.httpStatus = null;
  this.path = null;
  this.message = null;
 }

 public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
  timestamp = ZonedDateTime.now();
  this.httpStatus = httpStatus;
  this.path = path;
  this.message = message;
 }
}
