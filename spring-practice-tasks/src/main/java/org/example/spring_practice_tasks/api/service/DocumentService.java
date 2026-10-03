package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;

public interface DocumentService {

    DocumentResponseDto create(DocumentRequestDto documentRequestDto);

}
