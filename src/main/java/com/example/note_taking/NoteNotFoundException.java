package com.example.note_taking;

public class NoteNotFoundException extends RuntimeException {
  public NoteNotFoundException(String message) {
    super(message);
  }
}
