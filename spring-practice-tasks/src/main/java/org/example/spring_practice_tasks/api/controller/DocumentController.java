package org.example.spring_practice_tasks.api.controller;

import jakarta.validation.Valid;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DocumentController {

    @PostMapping(UrlConstants.DOCUMENT_URL)
    ResponseEntity<DocumentResponseDto> create(@Valid @RequestBody DocumentRequestDto documentRequestDto);

}
