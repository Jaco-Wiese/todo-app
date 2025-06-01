package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.dto.TodoUpdateDto;
import org.example.mapper.TodoMapper;
import org.example.repository.TodoRepository;
import org.example.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TodoService todoService;

    private TodoResponseDto sampleResponseDto;
    private TodoCreateDto createDto;
    private TodoUpdateDto updateDto;

    @BeforeEach
    public void setup() {
        sampleResponseDto = new TodoResponseDto(
            1L,
            "Test Todo",
            "Test Description",
            false,
            LocalDateTime.now(ZoneOffset.UTC),
            LocalDateTime.now(ZoneOffset.UTC)
        );

        createDto = new TodoCreateDto();
        createDto.setTitle("Test Todo");
        createDto.setDescription("Test Description");

        updateDto = new TodoUpdateDto();
        updateDto.setTitle("Updated Todo");
        updateDto.setDescription("Updated Description");
        updateDto.setCompleted(true);
    }

    @Test
    public void given_validCreateDto_when_createTodo_then_returnsCreatedTodo() throws Exception {
        // Given
		when(todoService.create(any(TodoCreateDto.class))).thenReturn(sampleResponseDto);

		// When
        final var result = mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        // Then
        result.andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").value(sampleResponseDto.id()))
              .andExpect(jsonPath("$.title").value(sampleResponseDto.title()))
              .andExpect(jsonPath("$.description").value(sampleResponseDto.description()))
              .andExpect(jsonPath("$.completed").value(sampleResponseDto.completed()));

        verify(todoService, times(1)).create(any(TodoCreateDto.class));
    }

    @Test
    public void given_todosExist_when_getAllTodos_then_returnsAllTodos() throws Exception {
        // Given
		when(todoService.getAll()).thenReturn(Collections.singletonList(sampleResponseDto));

        // When
        final var result = mockMvc.perform(get("/todos"));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$[0].id").value(sampleResponseDto.id()))
              .andExpect(jsonPath("$[0].title").value(sampleResponseDto.title()))
              .andExpect(jsonPath("$[0].description").value(sampleResponseDto.description()))
              .andExpect(jsonPath("$[0].completed").value(sampleResponseDto.completed()));

        verify(todoService, times(1)).getAll();
    }

    @Test
    public void given_todoExists_when_getTodoById_then_returnsTodo() throws Exception {
        // Given
        final var todoId = 1L;
        when(todoService.getById(todoId)).thenReturn(sampleResponseDto);

        // When
        final var result = mockMvc.perform(get("/todos/{id}", todoId));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(sampleResponseDto.id()))
              .andExpect(jsonPath("$.title").value(sampleResponseDto.title()))
              .andExpect(jsonPath("$.description").value(sampleResponseDto.description()))
              .andExpect(jsonPath("$.completed").value(sampleResponseDto.completed()));

        verify(todoService, times(1)).getById(todoId);
    }

    @Test
    public void given_validUpdateDto_when_updateTodo_then_returnsUpdatedTodo() throws Exception {
        // Given
        final var todoId = 1L;
        final var updatedResponseDto = new TodoResponseDto(
            todoId,
            updateDto.getTitle(),
            updateDto.getDescription(),
            updateDto.isCompleted(),
            sampleResponseDto.createdAt(),
            LocalDateTime.now()
        );

        when(todoService.update(eq(todoId), any(TodoUpdateDto.class))).thenReturn(updatedResponseDto);

        // When
        final var result = mockMvc.perform(put("/todos/{id}", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(updatedResponseDto.id()))
              .andExpect(jsonPath("$.title").value(updatedResponseDto.title()))
              .andExpect(jsonPath("$.description").value(updatedResponseDto.description()))
              .andExpect(jsonPath("$.completed").value(updatedResponseDto.completed()));

        verify(todoService, times(1)).update(eq(todoId), any(TodoUpdateDto.class));
    }

    @Test
    public void given_todoExists_when_deleteTodo_then_returnsNoContent() throws Exception {
        // Given
        final var todoId = 1L;
        doNothing().when(todoService).delete(todoId);

        // When
        final var result = mockMvc.perform(delete("/todos/{id}", todoId));

        // Then
        result.andExpect(status().isNoContent());
        verify(todoService, times(1)).delete(todoId);
    }

    @Test
    public void given_invalidCreateDto_when_createTodo_then_returnsBadRequest() throws Exception {
        // Given
        final var invalidDto = new TodoCreateDto();
        invalidDto.setTitle(""); // Empty title should fail validation
        invalidDto.setDescription("Test Description");

        // When
        final var result = mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        // Then
        result.andExpect(status().isBadRequest());
        verify(todoService, never()).create(any(TodoCreateDto.class));
    }

    @Test
    public void given_invalidUpdateDto_when_updateTodo_then_returnsBadRequest() throws Exception {
        // Given
        final var todoId = 1L;
        final var invalidDto = new TodoUpdateDto();
        invalidDto.setTitle(""); // Empty title should fail validation
        invalidDto.setDescription("Updated Description");
        invalidDto.setCompleted(true);

        // When
        final var result = mockMvc.perform(put("/todos/{id}", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        // Then
        result.andExpect(status().isBadRequest());
        verify(todoService, never()).update(eq(todoId), any(TodoUpdateDto.class));
    }

    @Test
    public void given_nonExistentTodoId_when_getTodoById_then_returnsNotFound() throws Exception {
        // Given
        final var todoId = 999L;
        when(todoService.getById(todoId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        // When
        final var result = mockMvc.perform(get("/todos/{id}", todoId));

        // Then
        result.andExpect(status().isNotFound());
        verify(todoService, times(1)).getById(todoId);
    }

    @Test
    public void given_nonExistentTodoId_when_updateTodo_then_returnsNotFound() throws Exception {
        // Given
        final var todoId = 999L;
        when(todoService.update(eq(todoId), any(TodoUpdateDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        // When
        final var result = mockMvc.perform(put("/todos/{id}", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        result.andExpect(status().isNotFound());
        verify(todoService, times(1)).update(eq(todoId), any(TodoUpdateDto.class));
    }

    @Test
    public void given_nonExistentTodoId_when_deleteTodo_then_returnsNotFound() throws Exception {
        // Given
        final var todoId = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"))
                .when(todoService).delete(todoId);

        // When
        final var result = mockMvc.perform(delete("/todos/{id}", todoId));

        // Then
        result.andExpect(status().isNotFound());
        verify(todoService, times(1)).delete(todoId);
    }
}
