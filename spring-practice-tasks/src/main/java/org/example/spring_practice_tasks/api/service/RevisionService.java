package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.RevisionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RevisionService {
    Page<RevisionResponseDto> getAllHistory(Pageable pageable);
}
