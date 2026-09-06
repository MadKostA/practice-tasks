-- V1__create_document_events_table.sql
CREATE TABLE IF NOT EXISTS document_events (
    event_type String,
    document_id String NULL,
    doc_status String NULL,
    http_status UInt16,
    event_time DateTime DEFAULT now(),
    processing_ms UInt32 DEFAULT 0,
    error_type String NULL
) ENGINE = MergeTree()
ORDER BY (event_time, event_type);
