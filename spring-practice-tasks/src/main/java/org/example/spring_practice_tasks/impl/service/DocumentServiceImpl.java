package org.example.spring_practice_tasks.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;
import org.example.spring_practice_tasks.api.enums.DocumentRequestEventStatus;
import org.example.spring_practice_tasks.api.enums.DocumentStatus;
import org.example.spring_practice_tasks.api.exceptions.DocumentEventException;
import org.example.spring_practice_tasks.api.service.DocumentEventLogService;
import org.example.spring_practice_tasks.api.service.DocumentService;
import org.example.spring_practice_tasks.impl.entity.DocumentEntity;
import org.example.spring_practice_tasks.impl.repo.DocumentRepository;
import org.example.spring_practice_tasks.impl.util.DocumentEntityMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentEntityMapper documentEntityMapper;
    private final DocumentEventLogService documentEventLogService;

    @Override
    public DocumentResponseDto create(DocumentRequestDto documentRequestDto) {
        log.info("Creating document: {}", documentRequestDto);
        long startTime = System.currentTimeMillis();
        UUID documentId = UUID.randomUUID();

        DocumentEntity saved;
        try {
            DocumentEntity requestEntity = documentEntityMapper.toEntity(documentId,
                    documentRequestDto, DocumentStatus.NEW.name());

            saved = documentRepository.save(requestEntity);
        } catch (RuntimeException e) {
            logEvent(DocumentRequestEventStatus.REQUEST_FAILED, null, null, 500,
                    LocalDateTime.now(ZoneId.systemDefault()), 0, e.getClass().getSimpleName());

            log.error("Failed document creation: {}", e.getMessage(), e);
            throw new DocumentEventException("Возникла ошибка во время создания документа: " + e.getMessage());
        }

        long processingMs = System.currentTimeMillis() - startTime;
        LocalDateTime time = LocalDateTime.ofInstant(saved.getCreatedAt(), ZoneId.systemDefault());

        logEvent(DocumentRequestEventStatus.REQUEST_RECEIVED, documentId.toString(),
                DocumentStatus.NEW.name(), 200, time, processingMs, null);

        DocumentResponseDto responseDto = documentEntityMapper.toResponseDto(saved);
        log.info("Created document with id={}", responseDto.id());
        return responseDto;
    }

    private void logEvent(DocumentRequestEventStatus eventStatus, String documentId, String documentStatus,
                          int httpStatus, LocalDateTime createdAt, long processingMs, String errorType) {
        try {
            documentEventLogService.writeLogEventAsync(
                    eventStatus.name(), documentId, documentStatus,
                    httpStatus, createdAt, processingMs, errorType
            );
        } catch (RuntimeException e) {
            log.error("Failed to write {} event: {}", eventStatus, e.getMessage());
        }
    }

    private static List<List<DocumentEntity>> partition(List<DocumentEntity> list, int size) {
        List<List<DocumentEntity>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return result;
    }
}

