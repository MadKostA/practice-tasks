package org.example.spring_practice_tasks.api.repo;

import org.example.spring_practice_tasks.impl.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

}
