package org.example.spring_practice_tasks.impl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revisions")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class NoteRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(name = "old_title")
    private String oldTitle;

    @Column(name = "old_text", columnDefinition = "TEXT")
    private String oldText;

    @Column(name = "changed_at")
    private Instant changedAt;

    @PrePersist
    public void onCreate() {
        changedAt = Instant.now();
    }

}
