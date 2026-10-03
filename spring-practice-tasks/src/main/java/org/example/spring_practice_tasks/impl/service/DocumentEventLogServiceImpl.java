package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.repo.DocumentEventLogRepository;
import org.example.spring_practice_tasks.api.service.DocumentEventLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class DocumentEventLogServiceImpl implements DocumentEventLogService {

    private final Executor documentEventLogExecutor;
    private final DocumentEventLogRepository documentEventLogRepository;

    public DocumentEventLogServiceImpl(Executor documentEventLogExecutor,
                                       DocumentEventLogRepository documentEventLogRepository) {
        this.documentEventLogExecutor = documentEventLogExecutor;
        this.documentEventLogRepository = documentEventLogRepository;
    }

    @Override
    public void writeLogEventAsync(String eventType, String documentId, String docStatus, int httpStatus,
                                   LocalDateTime createdAt, long processingMs, String errorType) {
        documentEventLogExecutor.execute(() -> {
            try {
                log.debug("Send document event with to log: id={}, eventType={}, docStatus={}", documentId, eventType, docStatus);
                documentEventLogRepository.logEvent(eventType, documentId, docStatus, httpStatus, createdAt, processingMs, errorType);
                log.debug("Logging event {} for document {} was finished", eventType, documentId);
            } catch (Exception e) {
                log.error("Failed to log event to ClickHouse: id={}, eventType={}, docStatus={}; exceptionMessage={}",
                        documentId, eventType, docStatus, e.getMessage());
            }
        });
    }
}
