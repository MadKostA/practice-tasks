package org.example.spring_practice_tasks.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;
import org.example.spring_practice_tasks.api.enums.DocumentStatus;
import org.example.spring_practice_tasks.api.service.DocumentService;
import org.example.spring_practice_tasks.impl.entity.DocumentEntity;
import org.example.spring_practice_tasks.impl.repo.DocumentRepository;
import org.example.spring_practice_tasks.impl.util.DocumentEntityMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentEntityMapper documentEntityMapper;

    @Override
    public DocumentResponseDto create(DocumentRequestDto documentRequestDto) {
        log.info("Creating document: {}", documentRequestDto);

        DocumentEntity requestEntity = documentEntityMapper.toEntity(UUID.randomUUID(),
                documentRequestDto, DocumentStatus.NEW.name());

        DocumentEntity saved = documentRepository.save(requestEntity);

        DocumentResponseDto responseDto = documentEntityMapper.toResponseDto(saved);

        log.info("Created document with id={}", responseDto.id());
        return responseDto;
    }
}

