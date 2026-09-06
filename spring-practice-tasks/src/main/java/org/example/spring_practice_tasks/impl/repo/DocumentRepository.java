package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.impl.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, UUID> {

    List<DocumentEntity> findAllByStatus(String status);

}
