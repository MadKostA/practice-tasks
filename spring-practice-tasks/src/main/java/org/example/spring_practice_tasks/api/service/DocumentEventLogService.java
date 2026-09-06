package org.example.spring_practice_tasks.api.service;

import java.time.LocalDateTime;

public interface DocumentEventLogService {

    void writeLogEventAsync(String eventType, String documentId, String docStatus, int httpStatus,
                            LocalDateTime createdAt, long processingMs, String errorType);

}
