package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.impl.entity.NoteEventLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteEventLogRepository extends CrudRepository<NoteEventLog, UUID> {
}
