package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.dto.TodoUpdateDto;
import org.example.mapper.TodoMapper;
import org.example.model.Todo;
import org.example.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

	private final TodoRepository todoRepository;

	@Override
	@Transactional
	public TodoResponseDto create(TodoCreateDto request) {
		final var entity = TodoMapper.toEntity(request);
		final var result = todoRepository.save(entity);

		return TodoMapper.toResponse(result);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TodoResponseDto> getAll() {
		final var entities = todoRepository.findAll();

		return entities.stream().map(TodoMapper::toResponse).toList();
	}


	@Override
	@Transactional(readOnly = true)
	public TodoResponseDto getById(Long id) {
		final var entity = findEntityById(id);

		return TodoMapper.toResponse(entity);
	}

	@Override
	@Transactional
	public TodoResponseDto update(Long id, TodoUpdateDto request) {
		final var entity = findEntityById(id);

		entity.setTitle(request.getTitle());
		entity.setDescription(request.getDescription());
		entity.setCompleted(request.isCompleted());

		final var result = todoRepository.save(entity);

		return TodoMapper.toResponse(result);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		final var entity = findEntityById(id);
		todoRepository.delete(entity);
	}

	private Todo findEntityById(Long id) {
		return todoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
	}
}
