package org.example.spring_practice_tasks.impl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "note_event_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteEventLog {

    @Id
    private UUID eventId;

    private UUID noteId;

    private String author;

    private String eventType;

    private Instant occurredAt;

    private Instant receivedAt;

    @PrePersist
    public void onCreate() {
        receivedAt = Instant.now();
    }
}
