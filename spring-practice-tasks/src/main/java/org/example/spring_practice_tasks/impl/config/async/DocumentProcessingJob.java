package org.example.spring_practice_tasks.impl.config.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.enums.DocumentRequestEventStatus;
import org.example.spring_practice_tasks.api.enums.DocumentStatus;
import org.example.spring_practice_tasks.api.service.DocumentEventLogService;
import org.example.spring_practice_tasks.impl.entity.DocumentEntity;
import org.example.spring_practice_tasks.impl.repo.DocumentRepository;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessingJob {

    private final DocumentRepository documentRepository;
    private final MongoTemplate mongoTemplate;
    private final DocumentEventLogService documentEventLogService;

    private static final int BATCH_SIZE = 5;
    private static final String STATUS_FIELD = "status";
    private static final String ID_FIELD = "_id";
    private static final String UPDATED_AT_FIELD = "updatedAt";

    private final Executor documentBatchProcessingExecutor;

    @Scheduled(cron = "${mongodb.doc-processing.cron}")
    public void processDocuments() {
        log.info("Starting document processing job");
        List<DocumentEntity> newDocs = documentRepository.findAllByStatus(DocumentStatus.NEW.name());
        if (newDocs.isEmpty()) {
            log.info("No NEW documents found");
            return;
        }

        log.debug("Found {} documents in NEW status", newDocs.size());

        List<List<DocumentEntity>> batches = splitToBatches(newDocs);

        log.debug("Split into {} batches of up to {}", batches.size(), BATCH_SIZE);

        startParallelBatchesProcessing(batches);

        log.info("Document processing job finished: {} documents processed in {} batches", newDocs.size(), batches.size());
    }

    private void startParallelBatchesProcessing(List<List<DocumentEntity>> batches) {
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> processBatch(batch), documentBatchProcessingExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processBatch(List<DocumentEntity> batch) {
        List<UUID> batchIds = batch.stream()
                .map(DocumentEntity::getId)
                .collect(Collectors.toList());

        log.debug("Batch start on thread {}: {} docs, ids={}", Thread.currentThread().getName(), batch.size(), batchIds);

        batchUpdateDocuments(batchIds);

        LocalDateTime eventTime = LocalDateTime.now(ZoneId.systemDefault());
        for (UUID id : batchIds) {
            documentEventLogService.writeLogEventAsync(
                    DocumentRequestEventStatus.STATUS_CHANGED.name(), id.toString(),
                    DocumentStatus.PROCESSED.name(), 200,
                    eventTime, 0, null
            );
        }
        log.debug("Batch done on thread {}: {} docs", Thread.currentThread().getName(), batch.size());
    }

    private void batchUpdateDocuments(List<UUID> documentIds) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DocumentEntity.class);
        Query query = new Query(Criteria.where(ID_FIELD).in(documentIds)
                .and(STATUS_FIELD)
                .is(DocumentStatus.NEW.name()));
        Update update = Update.update(STATUS_FIELD, DocumentStatus.PROCESSED.name())
                .set(UPDATED_AT_FIELD, Instant.now());
        bulkOps.updateMulti(query, update);
        bulkOps.execute();

        log.debug("Bulk update on thread {}: {} docs", Thread.currentThread().getName(), documentIds.size());
    }

    private List<List<DocumentEntity>> splitToBatches(List<DocumentEntity> documents) {
        List<List<DocumentEntity>> batches = new ArrayList<>();
        for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
            batches.add(documents.subList(i, Math.min(documents.size(), i + BATCH_SIZE)));
        }
        return batches;
    }
}
