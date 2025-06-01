package org.example.dto;

import java.time.LocalDateTime;

public record TodoResponseDto(
    Long id,
    String title,
    String description,
    Boolean completed,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
