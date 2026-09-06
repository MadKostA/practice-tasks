package org.example.spring_practice_tasks.impl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.controller.DocumentController;
import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;
import org.example.spring_practice_tasks.api.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class DocumentControllerImpl implements DocumentController {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<DocumentResponseDto> create(DocumentRequestDto documentRequestDto) {
        log.info("Request to create document : {}", documentRequestDto);

        DocumentResponseDto responseDto = documentService.create(documentRequestDto);

        log.info("Request to creating document was finished");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }
}
