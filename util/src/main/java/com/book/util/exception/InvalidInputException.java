package com.book.util.exception;

/**
 * Customized Exception class extended from unchecked exception {@link RuntimeException}
 * as Spring catch checked exception and wraps it as a Uncheck.
 *
 * @author Alexander Bravo
 */
public class InvalidInputException extends RuntimeException{
 public InvalidInputException() {
 }

 public InvalidInputException(String message) {
  super(message);
 }

 public InvalidInputException(String message, Throwable cause) {
  super(message, cause);
 }

 public InvalidInputException(Throwable cause) {
  super(cause);
 }
}
