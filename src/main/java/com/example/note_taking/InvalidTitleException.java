package com.example.note_taking;

public class InvalidTitleException extends RuntimeException {
  public InvalidTitleException(String message) {
    super(message);
  }
}
