package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.dto.TodoUpdateDto;
import org.example.mapper.TodoMapper;
import org.example.repository.TodoRepository;
import org.example.service.TodoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TodoService service;
	@Autowired
	private TodoRepository repository;

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

	@AfterEach
	public void tearDown() {
		repository.deleteAll();
	}

	@Test
	public void given_validCreateDto_when_createTodo_then_returnsCreatedTodo() throws Exception {
		// Given & When
		final var result = mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createDto)));

		// Then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(sampleResponseDto.id()))
			.andExpect(jsonPath("$.title").value(sampleResponseDto.title()))
			.andExpect(jsonPath("$.description").value(sampleResponseDto.description()))
			.andExpect(jsonPath("$.completed").value(sampleResponseDto.completed()));
	}

	@Test
	public void given_todosExist_when_getAllTodos_then_returnsAllTodos() throws Exception {
		// Given
		final var todo = TodoMapper.toEntity(createDto);
		repository.save(todo);

		// When
		final var result = mockMvc.perform(get("/todos"));

		// Then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].title").value(sampleResponseDto.title()))
			.andExpect(jsonPath("$[0].description").value(sampleResponseDto.description()))
			.andExpect(jsonPath("$[0].completed").value(sampleResponseDto.completed()));
	}

	@Test
	public void given_todoExists_when_getTodoById_then_returnsTodo() throws Exception {
		// Given
		final var todo = TodoMapper.toEntity(createDto);
		final var savedDto = repository.save(todo);

		// When
		final var result = mockMvc.perform(get("/todos/{id}", savedDto.getId()));

		// Then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(savedDto.getId()))
			.andExpect(jsonPath("$.title").value(todo.getTitle()))
			.andExpect(jsonPath("$.description").value(todo.getDescription()))
			.andExpect(jsonPath("$.completed").value(todo.getCompleted()));
	}

	@Test
	public void given_validUpdateDto_when_updateTodo_then_returnsUpdatedTodo() throws Exception {
		// Given
		final var todo = TodoMapper.toEntity(createDto);
		final var savedDto = repository.save(todo);

		// When
		final var result = mockMvc.perform(put("/todos/{id}", savedDto.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateDto)));

		// Then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(savedDto.getId()))
			.andExpect(jsonPath("$.title").value(updateDto.getTitle()))
			.andExpect(jsonPath("$.description").value(updateDto.getDescription()))
			.andExpect(jsonPath("$.completed").value(updateDto.isCompleted()));
	}

	@Test
	public void given_todoExists_when_deleteTodo_then_returnsNoContent() throws Exception {
		// Given
		final var todo = TodoMapper.toEntity(createDto);
		final var savedDto = repository.save(todo);

		// When
		final var result = mockMvc.perform(delete("/todos/{id}", savedDto.getId()));

		// Then
		result.andExpect(status().isNoContent());
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
	}

	@Test
	public void given_nonExistentTodoId_when_getTodoById_then_returnsNotFound() throws Exception {
		// Given
		final var todoId = 999L;

		// When
		final var result = mockMvc.perform(get("/todos/{id}", todoId));

		// Then
		result.andExpect(status().isNotFound());
	}

	@Test
	public void given_nonExistentTodoId_when_updateTodo_then_returnsNotFound() throws Exception {
		// Given
		final var todoId = 999L;

		// When
		final var result = mockMvc.perform(put("/todos/{id}", todoId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateDto)));

		// Then
		result.andExpect(status().isNotFound());
	}

	@Test
	public void given_nonExistentTodoId_when_deleteTodo_then_returnsNotFound() throws Exception {
		// Given
		final var todoId = 999L;

		// When
		final var result = mockMvc.perform(delete("/todos/{id}", todoId));

		// Then
		result.andExpect(status().isNotFound());
	}
}
