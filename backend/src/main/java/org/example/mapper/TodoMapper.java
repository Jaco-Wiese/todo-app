package org.example.mapper;

import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.model.Todo;

public class TodoMapper {

	public static Todo toEntity(TodoCreateDto todoCreateDto) {
		return new Todo(
			todoCreateDto.getTitle(),
			todoCreateDto.getDescription(),
			false
		);
	}

	public static TodoResponseDto toResponse(Todo todo) {
		return new TodoResponseDto(
			todo.getId(),
			todo.getTitle(),
			todo.getDescription(),
			todo.getCompleted(),
			todo.getCreatedAt(),
			todo.getUpdatedAt()
		);
	}

}
