package com.example.note_taking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps domain exceptions to HTTP status codes. */
@RestControllerAdvice
public class NoteExceptionHandler {

  @ExceptionHandler(NoteNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNotFound(NoteNotFoundException e) {
    return e.getMessage();
  }

  @ExceptionHandler(InvalidTitleException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleInvalidTitle(InvalidTitleException e) {
    return e.getMessage();
  }
}
