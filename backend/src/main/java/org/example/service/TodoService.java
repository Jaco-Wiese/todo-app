package org.example.service;

import org.example.dto.TodoCreateDto;
import org.example.dto.TodoResponseDto;
import org.example.dto.TodoUpdateDto;

import java.util.List;

public interface TodoService {

    TodoResponseDto create(TodoCreateDto request);
    List<TodoResponseDto> getAll();
    TodoResponseDto getById(Long id);
    TodoResponseDto update(Long id, TodoUpdateDto request);
    void delete(Long id);

}
