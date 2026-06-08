package com.example.note_taking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Stores each note as a single plain-text file ({title}.txt) inside a
 * configurable storage directory. The note's title is also its identifier and
 * its file name, so titles are validated to prevent escaping the directory.
 */
@Repository
public class NoteRepository {

  /** Allow letters, digits, dash, underscore and dot — but never a bare "..". */
  private static final Pattern VALID_TITLE = Pattern.compile("[A-Za-z0-9._-]+");

  private final Path storageDir;

  public NoteRepository(@Value("${notes.storage-dir:./data/notes}") Path storageDir) {
    this.storageDir = storageDir;
    try {
      Files.createDirectories(storageDir);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not create notes storage directory: " + storageDir, e);
    }
  }

  public void save(Note note) {
    Path file = fileFor(note.title());
    try {
      Files.writeString(file, note.content(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not save note: " + note.title(), e);
    }
  }

  public Optional<Note> findByTitle(String title) {
    Path file = fileFor(title);
    if (!Files.isRegularFile(file)) {
      return Optional.empty();
    }
    try {
      String content = Files.readString(file, StandardCharsets.UTF_8);
      return Optional.of(new Note(title, content));
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read note: " + title, e);
    }
  }

  /** Resolves the on-disk file for a title after validating it is safe. */
  private Path fileFor(String title) {
    validateTitle(title);
    return storageDir.resolve(title + ".txt");
  }

  private static void validateTitle(String title) {
    if (title == null || title.isBlank()) {
      throw new InvalidTitleException("Title must not be blank.");
    }
    if (title.contains("..") || !VALID_TITLE.matcher(title).matches()) {
      throw new InvalidTitleException(
          "Title may only contain letters, digits, '.', '-' and '_' and may not contain '..'.");
    }
  }
}
