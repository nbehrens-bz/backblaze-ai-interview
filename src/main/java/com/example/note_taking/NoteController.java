package com.example.note_taking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for writing and retrieving notes. No authorization is applied. */
@RestController
@RequestMapping("/notes")
public class NoteController {

  private final NoteRepository repository;

  public NoteController(NoteRepository repository) {
    this.repository = repository;
  }

  /** Writes (or overwrites) a note. The title is also the note's identifier. */
  @PostMapping
  public ResponseEntity<Void> create(@RequestBody Note note) {
    repository.save(note);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /** Retrieves a previously written note by its title. */
  @GetMapping("/{title}")
  public Note get(@PathVariable String title) {
    return repository
        .findByTitle(title)
        .orElseThrow(() -> new NoteNotFoundException("No note found with title: " + title));
  }
}
