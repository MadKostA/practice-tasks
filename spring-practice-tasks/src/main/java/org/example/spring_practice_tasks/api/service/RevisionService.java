package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface RevisionService {
    PageResponseDto getAllHistory(Pageable pageable);
}
