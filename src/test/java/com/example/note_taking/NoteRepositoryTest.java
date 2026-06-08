package com.example.note_taking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NoteRepositoryTest {

  @Test
  void saveThenFindByTitleRoundTripsTitleAndContent(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    repository.save(new Note("grocery-list", "milk, eggs, bread"));

    Optional<Note> found = repository.findByTitle("grocery-list");
    assertThat(found).contains(new Note("grocery-list", "milk, eggs, bread"));
  }

  @Test
  void findByTitleReturnsEmptyWhenNoteDoesNotExist(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    assertThat(repository.findByTitle("missing")).isEmpty();
  }

  @Test
  void saveOverwritesExistingNoteWithSameTitle(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    repository.save(new Note("note", "first"));
    repository.save(new Note("note", "second"));

    assertThat(repository.findByTitle("note")).contains(new Note("note", "second"));
  }

  @Test
  void saveRejectsTitleThatEscapesStorageDirectory(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    assertThatThrownBy(() -> repository.save(new Note("../escape", "x")))
        .isInstanceOf(InvalidTitleException.class);
  }

  @Test
  void saveRejectsTitleWithPathSeparator(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    assertThatThrownBy(() -> repository.save(new Note("a/b", "x")))
        .isInstanceOf(InvalidTitleException.class);
  }

  @Test
  void saveRejectsBlankTitle(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    assertThatThrownBy(() -> repository.save(new Note("   ", "x")))
        .isInstanceOf(InvalidTitleException.class);
  }

  @Test
  void findByTitleRejectsInvalidTitle(@TempDir Path dir) {
    NoteRepository repository = new NoteRepository(dir);

    assertThatThrownBy(() -> repository.findByTitle("../escape"))
        .isInstanceOf(InvalidTitleException.class);
  }
}
