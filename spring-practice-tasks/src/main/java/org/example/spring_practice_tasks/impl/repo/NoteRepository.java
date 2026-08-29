package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.api.dto.NoteAuthorStatsResponseDto;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("""
            SELECT new org.example.spring_practice_tasks.api.dto.NoteAuthorStatsResponseDto(COUNT(n), MAX(n.createdAt))
            FROM Note n WHERE n.author = :author GROUP BY n.author""")
    NoteAuthorStatsResponseDto findStatsByAuthor(@Param("author") String author);

}
