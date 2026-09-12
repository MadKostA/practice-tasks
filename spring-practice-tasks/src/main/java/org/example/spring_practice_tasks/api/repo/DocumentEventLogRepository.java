package org.example.spring_practice_tasks.api.repo;

import java.time.LocalDateTime;

public interface DocumentEventLogRepository {

    void logEvent(String eventType, String documentId, String docStatus, int httpStatus,
                  LocalDateTime createdAt, long processingMs, String errorType);

}
