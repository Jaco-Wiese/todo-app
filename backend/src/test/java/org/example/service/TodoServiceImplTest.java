package org.example.service;

import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.dto.TodoUpdateDto;
import org.example.model.Todo;
import org.example.mother.TodoMother;
import org.example.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for TodoServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class TodoServiceImplTest {

	@Mock
	private TodoRepository todoRepository;

	@InjectMocks
	private TodoServiceImpl todoService;

	private Todo todo1;
	private Todo todo2;

	@BeforeEach
	public void setup() {
		todo1 = new Todo();
		todo1.setId(1L);
		todo1.setTitle("Test Todo 1");
		todo1.setDescription("Test Description 1");
		todo1.setCompleted(false);


		todo2 = new Todo();
		todo2.setId(2L);
		todo2.setTitle("Test Todo 2");
		todo2.setDescription("Test Description 2");
		todo2.setCompleted(true);
	}

	@Test
	public void given_validCreateDto_when_create_then_returnsTodoResponseDto() {
		// Given
		final var createDto = new TodoCreateDto();
		createDto.setTitle("New Todo");
		createDto.setDescription("New Description");

		when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

		// When
		final var result = todoService.create(createDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.title()).isEqualTo("Test Todo 1");
		assertThat(result.description()).isEqualTo("Test Description 1");
		assertThat(result.completed()).isFalse();
		verify(todoRepository, times(1)).save(any(Todo.class));
	}

	@Test
	public void given_todos_when_getAll_then_returnsAllTodos() {
		// Given
		final var todos = Arrays.asList(todo1, todo2);
		when(todoRepository.findAll()).thenReturn(todos);

		// When
		final var result = todoService.getAll();

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).id()).isEqualTo(1L);
		assertThat(result.get(0).title()).isEqualTo("Test Todo 1");
		assertThat(result.get(1).id()).isEqualTo(2L);
		assertThat(result.get(1).title()).isEqualTo("Test Todo 2");
		verify(todoRepository, times(1)).findAll();
	}

	@Test
	public void given_existingTodoId_when_getById_then_returnsTodo() {
		// Given
		when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));

		// When
		final var result = todoService.getById(1L);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.title()).isEqualTo("Test Todo 1");
		assertThat(result.description()).isEqualTo("Test Description 1");
		assertThat(result.completed()).isFalse();
		verify(todoRepository, times(1)).findById(1L);
	}

	@Test
	public void given_nonExistentTodoId_when_getById_then_throwsNotFoundException() {
		// Given
		when(todoRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> todoService.getById(999L))
			.isInstanceOf(ResponseStatusException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

		verify(todoRepository, times(1)).findById(999L);
	}

	@Test
	public void given_existingTodoAndUpdateDto_when_update_then_returnsUpdatedTodo() {
		// Given
		final var updateDto = new TodoUpdateDto();
		updateDto.setTitle("Updated Title");
		updateDto.setDescription("Updated Description");
		updateDto.setCompleted(true);

		when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
		when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		final var result = todoService.update(1L, updateDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.title()).isEqualTo("Updated Title");
		assertThat(result.description()).isEqualTo("Updated Description");
		assertThat(result.completed()).isTrue();
		verify(todoRepository, times(1)).findById(1L);
		verify(todoRepository, times(1)).save(any(Todo.class));
	}

	@Test
	public void given_nonExistentTodoId_when_update_then_throwsNotFoundException() {
		// Given
		final var updateDto = new TodoUpdateDto();
		updateDto.setTitle("Updated Title");
		updateDto.setDescription("Updated Description");
		updateDto.setCompleted(true);

		when(todoRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> todoService.update(999L, updateDto))
			.isInstanceOf(ResponseStatusException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

		verify(todoRepository, times(1)).findById(999L);
		verify(todoRepository, never()).save(any(Todo.class));
	}

	@Test
	public void given_existingTodoId_when_delete_then_deletesTodo() {
		// Given
		when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
		doNothing().when(todoRepository).delete(any(Todo.class));

		// When
		todoService.delete(1L);

		// Then
		verify(todoRepository, times(1)).findById(1L);
		verify(todoRepository, times(1)).delete(any(Todo.class));
	}

	@Test
	public void given_nonExistentTodoId_when_delete_then_throwsNotFoundException() {
		// Given
		when(todoRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> todoService.delete(999L))
			.isInstanceOf(ResponseStatusException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

		verify(todoRepository, times(1)).findById(999L);
		verify(todoRepository, never()).delete(any(Todo.class));
	}
}
