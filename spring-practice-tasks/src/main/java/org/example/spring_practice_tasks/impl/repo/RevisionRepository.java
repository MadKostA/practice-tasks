package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevisionRepository extends JpaRepository<NoteRevision, UUID> {
}
