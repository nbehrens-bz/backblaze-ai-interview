package com.example.note_taking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private NoteRepository repository;

  @Test
  void postCreatesNoteAndReturns201() throws Exception {
    mockMvc
        .perform(
            post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"grocery-list\",\"content\":\"milk, eggs\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void postWithInvalidTitleReturns400() throws Exception {
    doThrow(new InvalidTitleException("bad title")).when(repository).save(any());

    mockMvc
        .perform(
            post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"../escape\",\"content\":\"x\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getReturnsExistingNote() throws Exception {
    when(repository.findByTitle("grocery-list"))
        .thenReturn(Optional.of(new Note("grocery-list", "milk, eggs")));

    mockMvc
        .perform(get("/notes/grocery-list"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("grocery-list"))
        .andExpect(jsonPath("$.content").value("milk, eggs"));
  }

  @Test
  void getMissingNoteReturns404() throws Exception {
    when(repository.findByTitle("missing")).thenReturn(Optional.empty());

    mockMvc.perform(get("/notes/missing")).andExpect(status().isNotFound());
  }

  @Test
  void getWithInvalidTitleReturns400() throws Exception {
    when(repository.findByTitle("bad")).thenThrow(new InvalidTitleException("bad title"));

    mockMvc.perform(get("/notes/bad")).andExpect(status().isBadRequest());
  }
}
