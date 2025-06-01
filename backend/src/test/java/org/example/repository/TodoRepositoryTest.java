package org.example.repository;

import org.example.mother.TodoMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoRepositoryTest {

	@Autowired
	private TodoRepository todoRepository;

	@AfterEach
	public void tearDown() {
		todoRepository.deleteAll();
	}

	@Test
	public void given_todo_when_save_then_savedEntityIsAsExpected() {
		// Given
		final var todo = new TodoMother().build();

		// When
		final var savedTodo = todoRepository.save(todo);

		// Then
		assertThat(savedTodo).isNotNull();
		assertThat(savedTodo.getTitle()).isEqualTo("Bake cookies");
		assertThat(savedTodo.getDescription()).isEqualTo("We need to bake cookies for the party tonight");
		assertThat(savedTodo.getCompleted()).isEqualTo(false);
		assertThat(savedTodo.getCreatedAt()).isNotNull();
		assertThat(savedTodo.getUpdatedAt()).isNotNull();
	}

	@Test
	public void given_savedEntity_when_findById_then_foundEntityIsAsExpected() {
		// Given
		final var todo = new TodoMother().build();
		final var savedTodo = todoRepository.save(todo);

		// When
		final var foundTodo = todoRepository.findById(savedTodo.getId());

		// Then
		assertThat(foundTodo).isNotNull();
		assertThat(foundTodo.get().getId()).isEqualTo(savedTodo.getId());
	}

	@Test
	public void given_multipleTodos_when_findAll_then_returnsAllTodos() {
		// Given
		final var firstTodo = new TodoMother()
			.withTitle("First Todo")
			.withDescription("First Description")
			.withCompleted(false)
			.build();
		todoRepository.save(firstTodo);

		final var secondTodo = new TodoMother()
			.withTitle("Second Todo")
			.withDescription("Second Description")
			.withCompleted(true)
			.build();
		todoRepository.save(secondTodo);

		// When
		final var todos = todoRepository.findAll();

		// Then
		assertThat(todos).hasSize(2);
	}


	@Test
	public void given_savedTodo_when_update_then_todoIsUpdatedAsExpected() {
		// Given
		final var todo = new TodoMother()
			.withTitle("Original Title")
			.withDescription("Original Description")
			.withCompleted(false)
			.build();
		final var savedTodo = todoRepository.save(todo);

		// When
		savedTodo.setTitle("Updated Title");
		savedTodo.setCompleted(true);
		final var updatedTodo = todoRepository.save(savedTodo);

		// Then
		assertThat(updatedTodo.getId()).isEqualTo(savedTodo.getId());
		assertThat(updatedTodo.getTitle()).isEqualTo("Updated Title");
		assertThat(updatedTodo.getDescription()).isEqualTo("Original Description");
		assertThat(updatedTodo.getCompleted()).isTrue();
	}


	@Test
	public void given_savedTodo_when_delete_then_todoIsNoLongerPresent() {
		// Given
		final var todo = new TodoMother()
			.withTitle("To Be Deleted")
			.withDescription("This todo will be deleted")
			.withCompleted(false)
			.build();
		final var savedTodo = todoRepository.save(todo);

		// When
		todoRepository.delete(savedTodo);

		// Then
		final var deletedTodo = todoRepository.findById(savedTodo.getId());
		assertThat(deletedTodo).isEmpty();
	}

}
