package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.api.repo.DocumentEventLogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class DocumentEventLogRepositoryImpl implements DocumentEventLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentEventLogRepositoryImpl(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void logEvent(String eventType, String documentId, String docStatus, int httpStatus,
                         LocalDateTime createdAt, long processingMs, String errorType) {
        String sql = """
                INSERT INTO document_events(event_type, document_id,
                 doc_status, http_status, event_time, processing_ms, error_type)
                 VALUES (?, ?, ?, ?, ?, ?, ?)""";
        jdbcTemplate.update(sql, eventType, documentId, docStatus,
                httpStatus, createdAt, processingMs, errorType);
    }

}
